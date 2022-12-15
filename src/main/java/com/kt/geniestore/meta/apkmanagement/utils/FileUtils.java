package com.kt.geniestore.meta.apkmanagement.utils;

import com.jcraft.jsch.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.Vector;

public class FileUtils {
    private Session session = null;
    private Channel channel = null;
    private ChannelSftp channelSftp = null;

    private static final Logger logger = LoggerFactory.getLogger(FileUtils.class);

    public void init(String host, String userName, String password, int port, String privateKey) {

        JSch jSch = new JSch();

        try {
            if(privateKey != null) {
                jSch.addIdentity(privateKey);
            }
            session = jSch.getSession(userName, host, port);

            if(privateKey == null && password != null) {
                session.setPassword(password);
            }

            // 프로퍼티 설정
            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no"); // 접속 시 hostkeychecking 여부
            session.setConfig(config);
            session.connect();
            //sftp로 접속
            channel = session.openChannel("sftp");
            channel.connect();
        } catch (JSchException e) {
            e.printStackTrace();
        }

        channelSftp = (ChannelSftp) channel;

    }

    public void mkdir(String dir, String mkdirName) {
        if (!this.exists(dir + "/" + mkdirName)) {
            try {
                channelSftp.cd(dir);
                channelSftp.mkdir(mkdirName);
            } catch (SftpException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public boolean exists(String path) {
        Vector res = null;
        try {
            res = channelSftp.ls(path);
        } catch (SftpException e) {
            if (e.id == ChannelSftp.SSH_FX_NO_SUCH_FILE) {
                return false;
            }
        }
        return res != null && !res.isEmpty();
    }

    public boolean upload(String homeDir, String path, MultipartFile mfile) throws IOException, SftpException {

        boolean isUpload = false;
        FileInputStream in = null;
        File targetDir = new File(path);

        channelSftp.cd(homeDir);

        if (!exists(homeDir + path)) {
            String[] folders = path.split("/");
            for(String folder : folders) {
                if(folder.length() > 0) {
                    try {
                        channelSftp.cd(folder);
                    } catch (SftpException e) {
                        channelSftp.mkdir(folder);
                        channelSftp.cd(folder);
                    }
                }
            }
        }

        try {
            File uploadFile = new File(mfile.getOriginalFilename());
            mfile.transferTo(uploadFile.getAbsoluteFile());

            in = new FileInputStream(uploadFile);
            channelSftp.cd(homeDir + path);
            channelSftp.put(in, uploadFile.getName());

            // 업로드했는지 확인
            if (this.exists(targetDir.getPath() +"/"+uploadFile.getName())) {
                isUpload = true;
            }
        } catch (SftpException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return isUpload;
    }

    public void disconnection() {
        channelSftp.quit();
        session.disconnect();
    }

    class MultipartInputStreamFileResource extends InputStreamResource {

        private final String filename;

        MultipartInputStreamFileResource(InputStream inputStream, String filename) {
            super(inputStream);
            this.filename = filename;
        }

        @Override
        public String getFilename() {
            return this.filename;
        }

        @Override
        public long contentLength() throws IOException {
            return -1;
        }
    }
}
