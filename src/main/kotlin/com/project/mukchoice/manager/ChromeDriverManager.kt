package com.project.mukchoice.manager

import jakarta.annotation.PostConstruct
import org.apache.commons.pool2.BasePooledObjectFactory
import org.apache.commons.pool2.ObjectPool
import org.apache.commons.pool2.PooledObject
import org.apache.commons.pool2.impl.DefaultPooledObject
import org.apache.commons.pool2.impl.GenericObjectPool
import org.apache.commons.pool2.impl.GenericObjectPoolConfig
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component
import java.time.Duration


@Configuration
class ChromeDriverManager {
    @PostConstruct
    fun initDriver() {
        // TODO  "C:\\bowon\\MukChoice\\chromedriver.exe" 패스 프로퍼티 적용할것.
        System.setProperty("webdriver.chrome.driver",  "C:\\bowon\\MukChoice\\chromedriver.exe")
    }

    companion object {
        val KAKAO_PLACE_SUMNAIL_CSS_SELECTOR = "div.inner_board div.col a.link_photo img"
        val logger: Logger = LoggerFactory.getLogger(ChromeDriverManager::class.java)

        private fun defaultPoolConfig(): GenericObjectPoolConfig<ChromeDriver> {
            return GenericObjectPoolConfig<ChromeDriver>().apply {
                maxTotal = 10 // 동시에 사용할 ChromeDriver 인스턴스 수 제한
                maxIdle = 3  // 유휴 상태의 ChromeDriver 인스턴스 수 제한
                minIdle = 1  // 최소 유휴 ChromeDriver 인스턴스 수
                blockWhenExhausted = true
            }
        }

        val defaultPoolConfig = defaultPoolConfig()
    }

    // 리소스 사용을 적게 하기위해 apache commons pool2를 사용하여 ChromeDriver를 관리.
    private val driverPool: ObjectPool<ChromeDriver> = GenericObjectPool(object : BasePooledObjectFactory<ChromeDriver>() {
        override fun create(): ChromeDriver = createDriver()
        override fun wrap(obj: ChromeDriver) = DefaultPooledObject(obj)
        override fun validateObject(p: PooledObject<ChromeDriver>?): Boolean {
            return try {
                p?.`object`?.title // WebDriver가 유효한지 확인
                true
            } catch (e: Exception) {
                false
            }
        }
    }, defaultPoolConfig.apply {
        testOnBorrow = true // 풀에서 객체를 빌릴 때 유효성 검사
    })

    private fun createDriver(): ChromeDriver {
        return ChromeDriver(ChromeOptions().apply {
            addArguments(
                "--disable-popup-blocking",  //팝업안띄움
                // "headless", //브라우저 안띄움
                "--disable-gpu",  //gpu 비활성화
                "--blink-settings=imagesEnabled=false", //이미지 다운 안받음
                "--no-sandbox", // 샌드박스 비활성화
                 "--disable-dev-shm-usage", // /dev/shm 사용 비활성화
                /* "--disable-extensions", // 확장 프로그램 비활성화
                "--disable-infobars", // 정보 표시줄 비활성화
                "--disable-notifications" // 알림 비활성화*/
            )
        })
    }

    fun getKakaoPlaceThumbnailUrl(url: String): String? {
        var driver: ChromeDriver? = null
        return try {
            driver = driverPool.borrowObject()
            val webDriverWait = WebDriverWait(driver, Duration.ofSeconds(10))
            driver.get(url)
            webDriverWait.until(
                ExpectedConditions.presenceOfElementLocated(By.cssSelector(KAKAO_PLACE_SUMNAIL_CSS_SELECTOR))
            )
            driver.findElement(By.cssSelector(KAKAO_PLACE_SUMNAIL_CSS_SELECTOR)).getAttribute("src")
        } catch (e: Exception) {
            logger.error("Error occurred while crawling thumbnail: ${e.message}", e)
            null
        } finally {
            driver?.let {
                //it.close()
                driverPool.returnObject(it)
                it.quit()
                //driverPool.returnObject(it)
            }
            // Thread.sleep(200)
        }
    }
}