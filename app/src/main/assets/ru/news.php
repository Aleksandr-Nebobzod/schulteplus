<!--
  ~ Copyright (c) 2024  "Smart Rovers"
  ~ Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
  ~ http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~ Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
  -->

<!DOCTYPE html>
<html lang="ru"  prefix="og: http://ogp.me/ns#">
    <head>
        <meta charset="UTF-8">
        <title>Новости ВНИМАНИЕ ШУЛЬТЕ ПЛЮС</title>
        <meta name="description" content="Совершенствуйте ваше сознание с помощью Внимание Шульте Плюс новости...">
        <meta name="keywords" content="news новости consciousness Schulte осознанность Шульте таблицы медитация саморазвитие развитие">
        <meta name="author" content="Astrobotic">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <meta name="HandheldFriendly" content="True" />
        <meta name="robots" content="index, follow" />
        <link rel="shortcut icon" href="http://attplus.in/favicon.png">
        <link rel="alternate" type="application/rss+xml" title="Новости Внимание Шульте Плюс" href="http://attplus.in/schulte/ru/rss.xml">


        <meta property="og:type" content="website">
        <meta property="og:title" content="Новости ВНИМАНИЕ ШУЛЬТЕ ПЛЮС">
        <meta property="og:description" content="Совершенствуйте ваше сознание с помощью Внимание Шульте Плюс, Новости">
        <meta name="twitter:card" content="summary">
        <meta name="twitter:title" content="Новости ВНИМАНИЕ ШУЛЬТЕ ПЛЮС">
        <meta name="twitter:description" content="Совершенствуйте ваше сознание с помощью Внимание Шульте Плюс, Новости">

        <!--    Icons-->
        <link href="https://fonts.googleapis.com/icon?family=Material+Icons+Round" rel="stylesheet">
        <!--    Fonts-->
        <link rel="preconnect" href="https://fonts.googleapis.com">
        <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
        <link href="https://fonts.googleapis.com/css2?family=Play:wght@400;700&display=swap" rel="stylesheet">
        <link href="https://fonts.googleapis.com/css2?family=Caveat:wght@400..700&family=Old+Standard+TT:ital,wght@0,400;0,700;1,400&display=swap" rel="stylesheet">

        <base href="/schulte/">
        <link rel="stylesheet" type="text/css" href="common/add_styles.css">
        <style>
            .news-item {
                display: flex;
                width: 100%;
                box-sizing: border-box;
                flex-wrap: wrap;
                margin-bottom: 20px;
                padding: 10px;
                border-bottom: 1px solid #ccc;
            }

            .news-item img {
                width: 40%;
                max-width: 40%;
                height: auto;
                margin: 5px 5px;
                float: right;
                border: 1px solid #ccc;
            }
        </style>
    </head>
<body>
<div class="body-header">
    <div class="mini-info">
        <a href="http://attplus.in/schulte/ru/index.html">ВНИМАНИЕ ШУЛЬТЕ ПЛЮС</a>
    </div>
    <h1>Новости ВНИМАНИЕ ШУЛЬТЕ ПЛЮС</h1>
    <address style="text-align: right;">"Просто хочешь ты знать,
        <br>Где и что происходит."
        <br>В.Цой
    </address>
</div>

<div class="container">
    <?php
        $html = file_get_contents($_SERVER['DOCUMENT_ROOT'] . '/schulte/ru/news_data.html');
        $dom = new DOMDocument();
        @$dom->loadHTML($html);

        // Находим элементы с классом "news-item"
        $xpath = new DOMXPath($dom);
        $news_items = $xpath->query("//div[@class='news-item']");

        $total_news = $news_items->length; // Количество новостей
        $news_per_page = 5; // Количество новостей на одной странице
        $total_pages = ceil($total_news / $news_per_page);

        $current_page = isset($_GET['page']) ? (int)$_GET['page'] : 1;
        $start_news = ($current_page - 1) * $news_per_page;
        $end_news = min($start_news + $news_per_page, $total_news);

        echo '<div class="mini-info">';
        echo "Найдено новостей: $total_news<br>";
        echo '</div>';
//        echo "Количество страниц: $total_pages<br>";
//        echo "Текущая страница: $current_page<br>";
//        echo "Отображаются новости с $start_news по $end_news<br>";

        // Отображаем новости для текущей страницы
        for ($i = $start_news; $i < $end_news; $i++) {
            echo $dom->saveHTML($news_items->item($i));
            }

        // Отображаем новости для текущей страницы с кнопкой копирования адреса
//        for ($i = $start_news; $i < $end_news; $i++) {
//            $item = $news_items->item($i);
//
//            // Проверяем, что элемент новости существует
//            if ($item) {
//                // Ищем элемент <a> внутри текущего элемента новости
//                $anchorElements = $item->getElementsByTagName('a');
//
//                // Проверяем, что элемент <a> найден
//                if ($anchorElements->length > 0) {
//                    $anchor = $anchorElements->item(0)->getAttribute('name'); // Получаем якорь
//                    echo str_replace('onclick="copyLink(\'https://example.com\')"', 'onclick="copyLink(\'http://attplus.in/schulte/ru/news_data.html#' . $anchor . '\')"', $dom->saveHTML($item));
//                } else {
//                    // Если элемент <a> не найден, просто выводим элемент без изменения
//                    echo $dom->saveHTML($item);
//                }
//            } else {
//                echo "Элемент новости не найден для индекса: $i"; // Для отладки
//            }
//        }

        // Пагинация
        if ($total_pages > 1) {
            echo '<div class="mini-info">';
            echo "Текущая страница: $current_page";
            echo " из $total_pages, перейти на стр.:<br></div><b>";
            for ($i = 1; $i <= $total_pages; $i++) {
                echo "<a href='/schulte/ru/news.php?page=$i'>$i</a>,  ";
            }
            echo '</b>';
        }
    ?>
</div>


    <?php
        //page self advertisement inside content
        include $_SERVER['DOCUMENT_ROOT'] . '/schulte/ru/add_footer.html';
    ?>


</body>
</html>
