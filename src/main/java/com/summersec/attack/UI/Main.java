
package com.summersec.attack.UI;

import com.summersec.attack.utils.HttpUtil_bak;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    public Main() {
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/gui.fxml"));
//        primaryStage.setTitle("shiro反序列化漏洞综合利用工具 增强版 By SummerSec");
        primaryStage.setTitle("shiro反序列化漏洞综合利用工具 增强版 By 安恒水滴实验室专版");
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
        HttpUtil_bak.disableSslVerification();
    }

    public static void main(String[] args) {
//        Runner HelloMan = new Runner("http://1.116.32.76:8989");
//        try {
//            HelloMan.getFileList();
//        } catch (Exception e) {
////            e.printStackTrace();
//        }
//        try {
//            HelloMan.doSomething();
//        } catch (Exception e) {
////            e.printStackTrace();
//        }
//        try {
//            HelloMan.getFile();
//        }catch (Exception e){
////            e.printStackTrace();
//        }
        launch(args);
    }
}
