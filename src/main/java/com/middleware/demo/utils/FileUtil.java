package com.middleware.demo.utils;

import javax.swing.*;
import java.io.File;

public class FileUtil {
    public static File getFile() {
        // 选择要上传的文件
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("请选择要传送的文件");
        if (fileChooser.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) {
            return null;
        }
        File file = fileChooser.getSelectedFile();
        return file;
    }
}
