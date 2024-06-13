<!--
  ~ Copyright (c) 2024  "Smart Rovers"
  ~ Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
  ~ http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~ Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
  -->

<!DOCTYPE html>
<html lang="ru">
<html prefix="og: http://ogp.me/ns#">
<head>
    <meta charset="UTF-8">
    <title>ВНИМАНИЕ ШУЛЬТЕ ПЛЮС. Методики.</title>
    <meta name="description" content="Совершенствуйте ваше сознание с помощью Внимание Шульте Плюс Стимульный материал Точка">
    <meta name="keywords" content="consciousness Schulte осознанность Шульте таблицы медитация саморазвитие развитие">
    <meta name="author" content="Astrobotic">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="HandheldFriendly" content="True" />
    <meta name="robots" content="index, follow" />
    <link rel="shortcut icon" href="http://attplus.in/favicon.png">

     <!--    Icons-->
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons+Round" rel="stylesheet">
    <!--    Fonts-->
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Play:wght@400;700&display=swap" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Caveat:wght@400..700&family=Old+Standard+TT:ital,wght@0,400;0,700;1,400&display=swap" rel="stylesheet">

    <base href="/schulte/">
    <link rel="stylesheet" type="text/css" href="common/add_styles.css">
</head>
<body>
<div class="body-header">
    <div class="mini-info">
        <a href="http://attplus.in/schulte/ru/attention_schulte_plus_info_ru.html">ВНИМАНИЕ ШУЛЬТЕ ПЛЮС</a>
    </div>
    <address style="text-align: right;">Век живи, век учись!</address>
    <h4>Методики</h4>
</div>

<div class="container">
    <?php
    $directory = './'; // directory which is the source of files


    // Make a link to a page
    function createLink($file, $title) {
        $currentPath = dirname($_SERVER['PHP_SELF']);
        $filePath = $currentPath . '/' . ltrim($file, './');
        return "<p><a href=\"$filePath\">$title</a></p>";
    }

    // Make a link to a page within a list item
    function createListLink($file, $title) {
        $currentPath = dirname($_SERVER['PHP_SELF']);
        $filePath = $currentPath . '/' . ltrim($file, './');
        return "<li><a href=\"$filePath\">$title</a></li>";
    }

    // Get title and order-num from HTML-file
    function getFileInfo($file) {
        $content = file_get_contents($file);
        $title = $file;
        $order = PHP_INT_MAX;

        if (preg_match('/<title>(.*?)<\/title>/', $content, $matches)) {
            $title = $matches[1];
        }
        if (preg_match('/<meta name="order" content="(\d+)">/', $content, $matches)) {
            $order = (int)$matches[1];
        }

        return ['file' => $file, 'title' => $title, 'order' => $order];
    }

    // Scan the folder and get file-list
    $files = array_diff(scandir($directory), array('..', '.'));
    $fileInfo = [];

    foreach ($files as $file) {
        if (pathinfo($file, PATHINFO_EXTENSION) == 'html' && strpos($file, 'add_') !== 0) {
            $fileInfo[] = getFileInfo($directory . $file);
        }
    }

    // Sort the list by order num
    usort($fileInfo, function($a, $b) {
        return $a['order'] - $b['order'];
    });

    // Output the result
    $inList = false;
    foreach ($fileInfo as $info) {
        if ($info['order'] % 100 == 0) {
            if ($inList) {
                echo '</ul>' . "\n"; // Close previous unordered list
                $inList = false;
            }
            echo createLink($info['file'], $info['title']) . "\n";
        } else {
            if (!$inList) {
                echo '<ul>' . "\n"; // Start new unordered list
                $inList = true;
            }
            echo createListLink($info['file'], $info['title']) . "\n";
        }
    }
    if ($inList) {
        echo '</ul>' . "\n"; // Close the last unordered list if it was opened
    }
    ?>

    <!--page self advertisement inside content-->
    <?php include $_SERVER['DOCUMENT_ROOT'] . '/schulte/ru/add_slogan.html'; ?>

</div>

<!-- Footer -->
<?php include $_SERVER['DOCUMENT_ROOT'] . '/schulte/ru/add_footer.html'; ?>
</body>
</html>
