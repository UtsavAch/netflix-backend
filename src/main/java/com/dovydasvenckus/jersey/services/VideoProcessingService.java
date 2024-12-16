package com.dovydasvenckus.jersey.services;

import com.dovydasvenckus.jersey.GoogleCloudStorageService;

import java.io.File;
import java.io.IOException;

public class VideoProcessingService {

    private final GoogleCloudStorageService storageService;

    public VideoProcessingService() throws IOException {
        this.storageService = new GoogleCloudStorageService();
    }

    public void processAndUploadVideo(String inputFilePath, String bucketName) throws IOException, InterruptedException {
        // Obter o nome do arquivo sem extensão para usar como diretório no bucket
        File inputFile = new File(inputFilePath);
        String fileNameWithoutExtension = inputFile.getName().substring(0, inputFile.getName().lastIndexOf('.'));
        String outputDir = "hls/" + fileNameWithoutExtension; // Diretório no bucket


        // Diretório temporário para salvar os arquivos HLS gerados
        File tempDir = new File(System.getProperty("java.io.tmpdir") + "/hls_output");
        if (!tempDir.exists()) {
            tempDir.mkdirs();
        }

        // Caminhos para os arquivos gerados
        String output1080pM3u8 = tempDir.getAbsolutePath() + "/1080p.m3u8";
        String output1080pSegments = tempDir.getAbsolutePath() + "/1080p_%03d.ts";
        String output360pM3u8 = tempDir.getAbsolutePath() + "/360p.m3u8";
        String output360pSegments = tempDir.getAbsolutePath() + "/360p_%03d.ts";

        // Comando FFmpeg
        String[] command = {
                "ffmpeg",
                "-i", inputFilePath,
                "-vf", "scale=1920:1080",
                "-c:voutput1080pSegments", "libx264", "-b:v", "4500k",
                "-c:a", "aac", "-b:a", "192k",
                "-hls_time", "10", "-hls_playlist_type", "vod",
                "-hls_segment_filename", output1080pSegments, output1080pM3u8,
                "-vf", "scale=640:360",
                "-c:v", "libx264", "-b:v", "800k",
                "-c:a", "aac", "-b:a", "128k",
                "-hls_time", "10", "-hls_playlist_type", "vod",
                "-hls_segment_filename", output360pSegments, output360pM3u8
        };

        // Executar o comando FFmpeg
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.redirectErrorStream(true); // Redirecionar erros para a saída padrão
        Process process = processBuilder.start();
        int exitCode = process.waitFor();

        if (exitCode != 0) {
            throw new RuntimeException("FFmpeg process failed with exit code " + exitCode);
        }

        // Upload dos arquivos HLS gerados para o Google Cloud Storage
        uploadGeneratedFilesToStorage(bucketName, outputDir, tempDir);

        // Limpar o diretório temporário
        deleteDirectory(tempDir);
    }

    private void uploadGeneratedFilesToStorage(String bucketName, String outputDir, File localDir) throws IOException {
        for (File file : localDir.listFiles()) {
            if (file.isFile()) {
                // Upload do arquivo para o diretório no bucket
                String cloudPath = outputDir + "/" + file.getName();
                storageService.uploadFile(bucketName, cloudPath, new String(java.nio.file.Files.readAllBytes(file.toPath())));
                System.out.println("Uploaded: " + file.getName() + " to " + cloudPath);
            }
        }
    }

    private void deleteDirectory(File directory) {
        for (File file : directory.listFiles()) {
            if (file.isDirectory()) {
                deleteDirectory(file);
            } else {
                file.delete();
            }
        }
        directory.delete();
    }
}

