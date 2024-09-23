
// Slide controller
var swiper = new Swiper('.swiper-container', {
    slidesPerView: 1,
    spaceBetween: 0,
    centeredSlides: false,
    navigation: {
        nextEl: '.swiper-button-next',
        prevEl: '.swiper-button-prev',
    },
    pagination: {
        el: '.swiper-pagination',
        clickable: true,
    },
    loop: false
});

function checkAllResults() {
    var totalResult = true;
    document.querySelectorAll('.swiper-slide .question input[type="checkbox"]').forEach(function(checkbox) {
        if (checkbox.checked !== (checkbox.getAttribute('data-correct') === 'true')) {
            totalResult = false;
        }
    });
    return totalResult;
}

// Функция для проверки, ответил ли пользователь хотя бы на один вопрос в каждом .question
function checkAllAnswered() {
    var allQuestions = document.querySelectorAll('.question');
    var allAnswered = true;

    allQuestions.forEach(function(question) {
        var checkboxes = question.querySelectorAll('input[type="checkbox"]');
        var answered = false;

        // Проверяем, есть ли хотя бы один отмеченный чекбокс
        checkboxes.forEach(function(checkbox) {
            if (checkbox.checked) {
                answered = true;
            }
        });

        // Если хотя бы в одном .question нет ответов, устанавливаем allAnswered в false
        if (!answered) {
            allAnswered = false;
        }
    });

    // Передаем результат в родительскую активность
    if (allAnswered) {
        AndroidInterface.onAllQuestionsAnswered(true);
    } else {
        AndroidInterface.onAllQuestionsAnswered(false);
    }
}

// Вызов функции при каждом изменении чекбоксов
document.querySelectorAll('input[type="checkbox"]').forEach(function(checkbox) {
    checkbox.addEventListener('change', function() {
        checkAllAnswered(); // Проверяем ответы при каждом изменении
    });
});
