package com.project.mukchoice.controller

import org.jsoup.Jsoup
import org.openqa.selenium.By
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.Duration


@RequestMapping("/sample")
@RestController
class SampleController {
    @GetMapping("/hello")
    fun hello(): String {
        return "Hello, World!"
    }

    @GetMapping("/shopImage")
    fun shopImage(): String {
     /*   val doc = Jsoup.connect("https://place.map.kakao.com/95749797")
            .userAgent("Mozilla/5.0")
            .get()*/

        System.setProperty("webdriver.chrome.driver", "C:\\bowon\\MukChoice\\chromedriver.exe");
        val driver = ChromeDriver()
        driver.get("https://place.map.kakao.com/95749797")
        Thread.sleep(1000);

        val html = driver.pageSource
        driver.quit()


        // 3. Jsoup으로 파싱 및 정보 추출
        val document = Jsoup.parse(html)
        val imgElement = document.select("div.board_photo.only_pc div.inner_board div.col a.link_photo img").first()
        val imageUrl = imgElement?.attr("src")

        return "Hello, World!"
    }

    @GetMapping("/shopImage2")
    fun shopImage2(): String {
        System.setProperty("webdriver.chrome.driver", "C:\\bowon\\MukChoice\\chromedriver.exe");
        val options = ChromeOptions()
        options.addArguments("--disable-popup-blocking") //팝업안띄움
        options.addArguments("headless") //브라우저 안띄움
        options.addArguments("--disable-gpu") //gpu 비활성화
        options.addArguments("--blink-settings=imagesEnabled=false") //이미지 다운 안받음
        val driver = ChromeDriver(options)
        val webDriverWait = WebDriverWait(driver, Duration.ofSeconds(10))
        driver.get("https://place.map.kakao.com/95749797")

        //cssSelector로 선택한 부분이 존재할때까지 wait
        webDriverWait.until(
            ExpectedConditions.presenceOfElementLocated(By.cssSelector("div.inner_board div.col a.link_photo img"))
        );

        val elemant = driver.findElement(By.cssSelector("div.inner_board div.col a.link_photo img"))
        val imageUrl = elemant.getAttribute("src")

        return "Hello, World!"
    }

    @GetMapping("/jsoupImage")
    fun jsoupImage(): String {
        // Jsoup으로 HTML 문서 가져오기
        val map = mapOf(
            "Origin" to "https://place-api.map.kakao.com",
            "Referer" to "https://place-api.map.kakao.com"
        )
        val doc = Jsoup.connect("https://place-api.map.kakao.com/places/panel3/90725902")
            .headers(map)
            .userAgent("Mozilla/5.0")
            .get()

        val ogImage = doc.select("meta[property=og:image]").attr("content")

        // 원하는 이미지 태그 선택
        /*val imgElement = doc.select("div.board_photo.only_pc div.inner_board div.col a.link_photo img").first()
        val imageUrl = imgElement?.attr("src") ?: "이미지 없음"*/

        return doc.body().text()
    }

    @GetMapping("/jsoupImage2")
    fun jsoupReview(): String? {
        // Jsoup으로 HTML 문서 가져오기
     /*   val map = mapOf(
            "Origin" to "https://place-api.map.kakao.com",
            "Referer" to "https://place-api.map.kakao.com"
        )*/
        val doc = Jsoup.connect("https://place.map.kakao.com/1679172457")
            .userAgent("Mozilla/5.0")
            .get()

        val text = doc.select("h2.screen_out").text()

        // 원하는 이미지 태그 선택
        /*val imgElement = doc.select("div.board_photo.only_pc div.inner_board div.col a.link_photo img").first()
        val imageUrl = imgElement?.attr("src") ?: "이미지 없음"*/

        return text
    }
}