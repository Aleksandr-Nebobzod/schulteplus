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
    <title>Smart Rovers</title>
    <meta name="description" content="The independent development to Improve your consciousness">
    <meta name="keywords" content="consciousness Schulte">
    <meta name="author" content="Astrobotic">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="HandheldFriendly" content="True" />
    <meta name="robots" content="index, follow" />
    <link rel="shortcut icon" href="http://attplus.in/favicon.png">

    <MetaTags>
        <meta property="og:type" content="website">
        <meta property="og:title" content="SMART ROVERS DEVELOPER">
        <meta property="og:description" content="The independent development to Improve your consciousness">
        <meta property="og:image" content="http://attplus.in/common/ic_launcher_round.png">
        <meta property="og:url" content="http://attplus.in">
    </MetaTags>
    <meta name="twitter:card" content="summary_large_image">
    <meta name="twitter:title" content="SMART ROVERS DEVELOPER">
    <meta name="twitter:description" content="The independent development to Improve your consciousness">
    <meta name="twitter:image" content="http://attplus.in/common/ic_launcher_round.png">

    <link rel="icon" href="http://attplus.in/favicon.xml" type="image/svg+xml">
    <link rel="icon" href="http://attplus.in/favicon.png" type="image/png">

<!-- Yandex.Metrika counter attplus-y01ru -->
    <script type="text/javascript" src="http://attplus.in/schulte/common/yandex_metrica.js"></script>

    <noscript><div><img src="https://mc.yandex.ru/watch/98319797" style="position:absolute; left:-9999px;" alt="" /></div></noscript>
<!-- /Yandex.Metrika counter -->

<!-- Разметка JSON-LD, созданная Мастером разметки структурированных данных Google. -->
<script type="application/ld+json">
[
  {
    "@context": "http://schema.org",
    "@type": "SoftwareApplication",
    "name": "SMART ROVERS team",
    "url": "http://attplus.in/",
    "datePublished": "2024-04-02"
  }
]
</script>


</head>
<body>
<?php
function isMobile() {
    return preg_match('/(android|webos|iphone|ipad|ipod|blackberry|iemobile|opera mini|mobile)/i', $_SERVER['HTTP_USER_AGENT']);
}

if (isMobile()) {
    include('landing_mobile_version.html');
} else {
    include('landing_desktop_version.html');
}
?>
</body>
</html>
