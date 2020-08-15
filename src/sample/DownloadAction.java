package sample;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.opensymphony.xwork2.ActionSupport;
import lombok.Data;

/**
 *
 * S3から指定されたIDに紐づくファイルをダウンロードするためのActionクラス.
 *
 */
@Data
public class DownloadAction extends ActionSupport {

  private long id;

  /** ファイル出力用のストリーム */
  private InputStream inputStream;

  /** ファイル名 */
  private String fileName;

  /** エンコードしたファイル名(日本語のファイル名が文字化けすることを防ぐ） */
  private String encodedFileName;

  /** ダウンロードファイルサイズ */
  private long contentLength;

  /**
   *
   * S3からリクエストで渡されたIDに紐づくファイルをダウンロードする.
   *
   */
  public String execute() {

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

    // ダウンロードするファイルのバケット名とキー名(ファイル名)を設定する
    S3Object object =
        client.getObject(new GetObjectRequest(rb.getString("backetName"), fileMap.get(id)));

    // 返却する情報をセットする
    this.setInputStream(object.getObjectContent());
    this.setFileName(object.getKey());
    this.setContentLength(object.getObjectMetadata().getContentLength());
    try {
      this.setEncodedFileName(URLEncoder.encode(object.getKey(), "UTF-8"));
      return SUCCESS;
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
      return "error";
    }
  }
}
