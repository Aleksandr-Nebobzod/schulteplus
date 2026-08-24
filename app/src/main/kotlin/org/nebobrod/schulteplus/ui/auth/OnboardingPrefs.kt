package org.nebobrod.schulteplus.ui.auth

import android.content.Context
import org.nebobrod.schulteplus.Utils
import org.nebobrod.schulteplus.common.Const
import java.util.UUID

/**
 * Глобальное состояние онбординга (D-19): флаг показа + временный аноним (SP03-06/D-30).
 * Читается ДО входа, когда uid ещё неизвестен, поэтому живёт в prf_global
 * (а не в per-user prefs ExerciseRunner). Аноним: свой uid + случайное имя из
 * таблицы слов (Utils.getRandomName), живёт до logOut; кошелёк — prefs по uid
 * анонима (единый источник с ExerciseRunner.KEY_PSYCOINS).
 */
object OnboardingPrefs {

    /** Стартовый запас анонима (кредит макета, TODO SP-06: механика цен/кредита). */
    const val ANON_CREDIT = 10

    private fun global(context: Context) =
        context.getSharedPreferences(Const.GLOBAL_PREFS_NAME, Context.MODE_PRIVATE)

    private fun anonPrefs(context: Context) =
        context.getSharedPreferences(anonUid(context), Context.MODE_PRIVATE)

    fun isShown(context: Context): Boolean =
        global(context).getBoolean(Const.KEY_ONBOARDING_SHOWN, false)

    fun markShown(context: Context) {
        global(context).edit().putBoolean(Const.KEY_ONBOARDING_SHOWN, true).apply()
    }

    /** Создать анонима при первом показе онбординга: uid, случайное имя, стартовый запас. */
    fun ensureAnon(context: Context) {
        if (anonUid(context).isEmpty()) {
            val uid = UUID.randomUUID().toString()
            global(context).edit()
                .putString(Const.KEY_ONBOARDING_ANON_UID, uid)
                .putString(Const.KEY_ONBOARDING_ANON_NAME, Utils.getRandomName())
                .apply()
            anonPrefs(context).edit().putInt(Const.KEY_PSYCOINS, ANON_CREDIT).apply()
        }
    }

    fun anonUid(context: Context): String =
        global(context).getString(Const.KEY_ONBOARDING_ANON_UID, "").orEmpty()

    fun anonName(context: Context): String =
        global(context).getString(Const.KEY_ONBOARDING_ANON_NAME, "").orEmpty()

    /** Запас монет анонима (кошелёк). */
    fun anonBalance(context: Context): Int =
        anonPrefs(context).getInt(Const.KEY_PSYCOINS, 0)

    /** Списать цену за выбранное упражнение (SP03-06). */
    fun spend(context: Context, price: Int) {
        anonPrefs(context).edit().putInt(Const.KEY_PSYCOINS, anonBalance(context) - price).apply()
    }

    /** Вернуть цену, если пользователь ушёл на регистрацию (SP03-06/D-30). */
    fun refund(context: Context, price: Int) {
        anonPrefs(context).edit().putInt(Const.KEY_PSYCOINS, anonBalance(context) + price).apply()
    }

    /** Выбранное на слайде 2 упражнение — запустится после слайда 3 (автостарт, D-30). */
    fun anonExType(context: Context): String =
        anonPrefs(context).getString(Const.KEY_TYPE_OF_EXERCISE, "").orEmpty()

    /**
     * Записать выбранное упражнение + его настройки (SP03-12/14) в prefs анонима
     * (привязка — по uid анонима, тот же файл читает ExerciseRunner после getInstance(anon)).
     * optionId — внутренний id карточки онбординга; exTypeId — реальный ID из Const.
     */
    fun setAnonExType(context: Context, optionId: String) {
        val editor = anonPrefs(context).edit()
        when (optionId) {
            // Числа 3×3: S1 + арабские цифры (SP03-14: «gcb_sch_num» не существует в ex_types.json)
            "sch_num" -> {
                editor.putString(Const.KEY_TYPE_OF_EXERCISE, Const.KEY_PRF_EX_S1)
                    .putInt(Const.KEY_X_SIZE, 3).putInt(Const.KEY_Y_SIZE, 3)
                    .putString(Const.KEY_PRF_SYMBOLS, Const.KEY_SYMBOL_TYPE_NUMBER)
            }
            // Буквы 3×3: S1 + латиница
            "sch_letters" -> {
                editor.putString(Const.KEY_TYPE_OF_EXERCISE, Const.KEY_PRF_EX_S1)
                    .putInt(Const.KEY_X_SIZE, 3).putInt(Const.KEY_Y_SIZE, 3)
                    .putString(Const.KEY_PRF_SYMBOLS, Const.KEY_SYMBOL_TYPE_LETTER_LATIN)
            }
            // Биты 4×4: S1 + цифры; двоичные числа — бэклог (BACKLOG.md SP-08)
            "sch_bits" -> {
                editor.putString(Const.KEY_TYPE_OF_EXERCISE, Const.KEY_PRF_EX_S1)
                    .putInt(Const.KEY_X_SIZE, 4).putInt(Const.KEY_Y_SIZE, 4)
                    .putString(Const.KEY_PRF_SYMBOLS, Const.KEY_SYMBOL_TYPE_NUMBER)
            }
            "basics" -> editor.putString(Const.KEY_TYPE_OF_EXERCISE, Const.KEY_PRF_EX_B1)
            "sssr" -> editor.putString(Const.KEY_TYPE_OF_EXERCISE, Const.KEY_PRF_EX_R1)
        }
        // SP03-14: полный набор настроек Schulte — подсказки/отсчёт/перемешивание/квадрат ВКЛ;
        // ratings/prob ВЫКЛ (иначе loadPreference игнорирует prf_x_size — ветка if(ratings))
        editor.putBoolean(Const.KEY_PRF_HINTED, true)
            .putBoolean(Const.KEY_PRF_COUNT_DOWN, true)
            .putBoolean(Const.KEY_PRF_SHUFFLE, true)
            .putBoolean(Const.KEY_PRF_SQUARED, true)
            .putBoolean(Const.KEY_PRF_RATINGS, false)
            .putBoolean(Const.KEY_PRF_PROB_ENABLED, false)
            .apply()
    }

    /** Купленные упражнения анонима (SP03-11): повторный выбор — «0 псимонет». */
    fun isPurchased(context: Context, exTypeId: String): Boolean =
        anonPrefs(context).getStringSet(Const.KEY_ANON_PURCHASED, emptySet())?.contains(exTypeId) ?: false

    fun markPurchased(context: Context, exTypeId: String) {
        val set = anonPrefs(context).getStringSet(Const.KEY_ANON_PURCHASED, emptySet())?.toMutableSet()
            ?: mutableSetOf()
        set.add(exTypeId)
        anonPrefs(context).edit().putStringSet(Const.KEY_ANON_PURCHASED, set).apply()
    }

    /** Снять «купленность» при прерывании регистрацией (полный откат покупки, SP03-11). */
    fun unpurchase(context: Context, exTypeId: String) {
        val set = anonPrefs(context).getStringSet(Const.KEY_ANON_PURCHASED, emptySet())?.toMutableSet()
            ?: mutableSetOf()
        set.remove(exTypeId)
        anonPrefs(context).edit().putStringSet(Const.KEY_ANON_PURCHASED, set).apply()
    }

    /**
     * SP03-17: выход анонима из системы — сессия одноразовая, как в Firebase:
     * удаляется файл prefs анонима (кошелёк/покупки/настройки) и его uid/имя в prf_global.
     * Следующий вход «без регистрации» создаёт НОВОГО анонима (новый uid, стартовый кредит).
     */
    fun clearAnon(context: Context) {
        val uid = anonUid(context)
        if (uid.isNotEmpty()) {
            context.deleteSharedPreferences(uid)
        }
        global(context).edit()
            .remove(Const.KEY_ONBOARDING_ANON_UID)
            .remove(Const.KEY_ONBOARDING_ANON_NAME)
            .apply()
    }
}
