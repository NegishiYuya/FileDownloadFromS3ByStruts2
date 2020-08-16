package sample;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.amazonaws.HttpMethod;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.opensymphony.xwork2.ActionSupport;
import lombok.Data;

/**
 * 署名付きURLを使用してファイルをダウンロードするためのActionクラス.
 *
 */
@Data
public class DownloadFromPresignedUrlAction extends ActionSupport {

  /** リクエストとして送られる画像ID */
  private long id;

  /** ファイル出力用のストリーム */
  private InputStream inputStream;

  /** ファイル名 */
  private String fileName;

  /** エンコードしたファイル名(日本語のファイル名が文字化けすることを防ぐ） */
  private String encodedFileName;

  /** ダウンロードファイルサイズ */
  private long contentLength;

  /** ログ出力用のLoggerオブジェクト */
  private Logger logger = LogManager.getLogger();

  /**
   * 署名付きURLを使用してファイルをダウンロードする.
   */
  public String execute() throws IOException {

    // 動作確認用の画像IDとファイル名の紐づけを定義したMap
    Map<Long, String> fileMap = new HashMap<>(3);
    fileMap.put(1L, "sample.jpg");
    fileMap.put(2L, "sample2.jpg");

    // AWSへの認証情報を設定する
    ResourceBundle rb = ResourceBundle.getBundle("awsS3");
    AWSCredentials credentials =
        new BasicAWSCredentials(rb.getString("accessKeyId"), rb.getString("secretAccessKey"));

    // S3への接続用のクライアントを生成する
    AmazonS3 client = AmazonS3ClientBuilder.standard()
        // 認証情報
        .withCredentials(new AWSStaticCredentialsProvider(credentials))
        // リージョン
        .withRegion(Regions.AP_NORTHEAST_1).build();

    // 署名付きURLを生成する
    URL presignedUrl =
        this.generatePresignedUrl(rb.getString("backetName"), fileMap.get(id), 60L, client);

    // URLからファイル名を取得する
    String path = presignedUrl.getPath();
    String fileName = path.substring(path.lastIndexOf("/") + 1);

    // 返却する情報をセットする
    this.setInputStream(presignedUrl.openStream());
    this.setFileName(fileName);
    this.setEncodedFileName(URLEncoder.encode(fileName, "UTF-8"));
    HttpURLConnection httpConnection = (HttpURLConnection) presignedUrl.openConnection();
    this.setContentLength(httpConnection.getContentLengthLong());

    return SUCCESS;
  }

  /**
   *
   * バケット名、オブジェクトキー、有効期限(分)を指定して、S3オブジェクトの署名付きURLを生成する.
   *
   * @param backetName バケット名
   * @param objectKey オブジェクトキー
   * @param expirationMinute 有効期限(分)
   * @param client S3への接続用のクライアント
   * @return URL 署名付き URL
   */
  private URL generatePresignedUrl(String backetName, String objectKey, long expirationMinute,
      AmazonS3 client) {
    GeneratePresignedUrlRequest generatePresignedUrlRequest =
        new GeneratePresignedUrlRequest(backetName, objectKey).withMethod(HttpMethod.GET)
            .withExpiration(this.calcExpiration(expirationMinute));
    URL presignedUrl = client.generatePresignedUrl(generatePresignedUrlRequest);
    logger.info("presignedUrl: " + presignedUrl);
    return presignedUrl;
  }

  /**
   * 現在日時に引数で指定した分を足して有効期限を算出する.
   *
   * @param minute 分
   * @return Date 有効期限
   */
  private Date calcExpiration(long minute) {
    Date expiration = new Date();
    long expTimeMillis = expiration.getTime();
    expTimeMillis += 1000 * 60 * minute;
    expiration.setTime(expTimeMillis);
    logger.info("expiration: " + expiration);
    return expiration;
  }

}
