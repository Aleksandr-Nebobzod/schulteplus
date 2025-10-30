<!--
  ~ Copyright (c) 2024  "Smart Rovers"
  ~ Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
  ~ http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~ Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
  -->

<!DOCTYPE html>
<html lang="en" prefix="og: http://ogp.me/ns#">
    <head>
        <meta charset="UTF-8">
        <title>News ATTENTION SCHULTE PLUS</title>
        <meta name="description" content="Enhance your consciousness with Attention Schulte Plus news...">
        <meta name="keywords" content="news consciousness Schulte awareness Schulte tables meditation self-development growth">
        <meta name="author" content="Astrobotic">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <meta name="HandheldFriendly" content="True" />
        <meta name="robots" content="index, follow" />
        <link rel="shortcut icon" href="http://attplus.in/favicon.png">
        <link rel="alternate" type="application/rss+xml" title="News Attention Schulte Plus" href="http://attplus.in/schulte/en/rss.xml">

        <meta property="og:type" content="website">
        <meta property="og:title" content="News ATTENTION SCHULTE PLUS">
        <meta property="og:description" content="Enhance your consciousness with Attention Schulte Plus, News">
        <meta name="twitter:card" content="summary">
        <meta name="twitter:title" content="News ATTENTION SCHULTE PLUS">
        <meta name="twitter:description" content="Enhance your consciousness with Attention Schulte Plus, News">

        <!-- Icons -->
        <link href="https://fonts.googleapis.com/icon?family=Material+Icons+Round" rel="stylesheet">
        <!-- Fonts -->
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
        <a href="http://attplus.in/schulte/en/index.html">ATTENTION SCHULTE PLUS</a>
    </div>
    <h1>News ATTENTION SCHULTE PLUS</h1>
    <address style="text-align: right;">"You just want to know,
        <br>Where and what is happening."
        <br>V. Tsoi
    </address>
</div>

<div class="container">
    <?php
        $html = file_get_contents($_SERVER['DOCUMENT_ROOT'] . '/schulte/en/news_data.html');
        $dom = new DOMDocument();
        @$dom->loadHTML($html);

        // Find elements with class "news-item"
        $xpath = new DOMXPath($dom);
        $news_items = $xpath->query("//div[@class='news-item']");

        $total_news = $news_items->length; // Number of news items
        $news_per_page = 5; // Number of news items per page
        $total_pages = ceil($total_news / $news_per_page);

        $current_page = isset($_GET['page']) ? (int)$_GET['page'] : 1;
        $start_news = ($current_page - 1) * $news_per_page;
        $end_news = min($start_news + $news_per_page, $total_news);

        echo '<div class="mini-info">';
        echo "Total news found: $total_news<br>";
        echo '</div>';
//        echo "Total pages: $total_pages<br>";
//        echo "Current page: $current_page<br>";
//        echo "Displaying news from $start_news to $end_news<br>";

        // Display news for the current page
        for ($i = $start_news; $i < $end_news; $i++) {
            echo $dom->saveHTML($news_items->item($i));
        }

        // Pagination
        if ($total_pages > 1) {
            echo '<div class="mini-info">';
            echo "Current page: $current_page";
            echo " of $total_pages, go to page:<br></div><b>";
            for ($i = 1; $i <= $total_pages; $i++) {
                echo "<a href='/schulte/en/news.php?page=$i'>$i</a>,  ";
            }
            echo '</b>';
        }
    ?>
</div>

    <?php
        // Page self-advertisement inside content
        include $_SERVER['DOCUMENT_ROOT'] . '/schulte/en/add_footer.html';
    ?>
</body>
</html>

