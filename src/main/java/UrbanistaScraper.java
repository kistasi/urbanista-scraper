import java.util.ArrayList;
import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class UrbanistaScraper {
    public static void main(String[] args) throws IOException, InterruptedException {
        final String baseURL = "https://index.hu/urbanista/?p=";

        Document frontPage = Jsoup.connect(baseURL).get();
        String pages = frontPage.select("nav.pagination ul.second-line a.last").text();

        for (int i = 0; i <= Integer.parseInt(pages); i++) {
            UrbanistaScraper.parseResultsList(baseURL + i);
        }
    }

    private static void parseResultsList(String url) throws IOException, InterruptedException {
        Document resultsPage = Jsoup.connect(url).get();
        Elements Articles = resultsPage.select(".blog-poszt-cim a");
        int failedArticleDownloadCounter = 0;

        for (Element Article : Articles) {
            try {
                UrbanistaScraper.parseArticlePage(Article);
            } catch (IOException Exception) {
                failedArticleDownloadCounter++;
            } finally {
                System.out.println("Failed article downloads: " + failedArticleDownloadCounter);
            }
        }
    }

    private static void parseArticlePage(Element article) throws IOException, InterruptedException {
        Document articlePage = Jsoup.connect(article.attr("href")).get();

        articlePage.select(".nm_widget__wrapper").remove();
        articlePage.select(".microsite").remove();
        articlePage.select(".linkpreview").remove();
        articlePage.select(".cikk-bottom-box").remove();
        articlePage.select(".keretes").remove();
        articlePage.select("iframe").remove();

        String articleTitle = articlePage.select(".content-title h1 span").text();
        String articleSubtitle = articlePage.select(".alcim").text();
        String articleDate = articlePage.select(".datum").text();

        Elements articleAuthors = articlePage.select(".szerzok_container .szerzo");
        ArrayList<String> articleAuthorsList = new ArrayList<>();

        for (Element Author : articleAuthors) {
            articleAuthorsList.add(Author.select("a img").attr("alt"));
        }

        String articleContent = articlePage.select(".cikk-torzs p").toString();

        System.out.println(articleTitle);
        System.out.println(articleSubtitle);
        System.out.println(articleDate);
        System.out.println(articleAuthorsList);
        System.out.println(articleContent);
        System.out.println("==============================");

        Thread.sleep(500);
    }
}

