
package com.summersec.attack.UI;

import com.summersec.attack.utils.HttpUtil_bak;
import javafx.embed.swing.SwingFXUtils;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;

public class Main extends Application {
    public Main() {
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/gui.fxml"));
        primaryStage.setTitle("shiro反序列化漏洞综合利用工具 增强版 SummerSec");
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.getIcons().add(buildAppIcon());
        primaryStage.setMinWidth(1180.0D);
        primaryStage.setMinHeight(860.0D);
        primaryStage.show();
        HttpUtil_bak.disableSslVerification();
    }

    private Image buildAppIcon() {
        BufferedImage image = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        try {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setPaint(new GradientPaint(0, 0, new Color(15, 31, 51), 64, 64, new Color(31, 111, 235)));
            g.fill(new RoundRectangle2D.Double(0, 0, 64, 64, 16, 16));

            g.setColor(new Color(220, 238, 255));
            int[] shieldX = new int[]{32, 49, 45, 32, 19, 15};
            int[] shieldY = new int[]{8, 17, 41, 56, 41, 17};
            g.fillPolygon(shieldX, shieldY, 6);

            g.setColor(new Color(15, 31, 51));
            g.fillRoundRect(28, 22, 8, 18, 4, 4);
            g.fillOval(23, 30, 18, 18);

            g.setColor(new Color(255, 255, 255, 180));
            g.setStroke(new BasicStroke(2f));
            g.drawRoundRect(5, 5, 54, 54, 14, 14);
        } finally {
            g.dispose();
        }
        return SwingFXUtils.toFXImage(image, null);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
