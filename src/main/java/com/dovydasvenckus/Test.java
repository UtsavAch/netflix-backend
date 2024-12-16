package com.dovydasvenckus;

import java.util.ArrayList;
import java.util.List;

public class Test {

    public static void main(String[] args) {
        // Simulando o conteúdo de um arquivo .m3u8
        String m3u8Content = "#EXTM3U\n#EXT-X-VERSION:3\n#EXT-X-TARGETDURATION:10\n#EXTINF:10,\n1080p_000.ts\n#EXTINF:10,\nhttps://storage.googleapis.com/bucket-movies-project/hls/big_buck_bunny/1080p_001.ts?X-Goog-Algorithm=GOOG4-RSA-SHA256&X-Goog-Credential=...\n#EXT-X-ENDLIST";


        // Criando uma instância da classe de teste
        Test main = new Test();

        // Extraindo os caminhos .ts do conteúdo do .m3u8
        List<String> extractedTsPaths = main.extractTsFilePathsFromPlaylist(m3u8Content);

        // Imprimindo o resultado
        System.out.println("Caminhos extraídos:");
        for (String tsPath : extractedTsPaths) {
            System.out.println(tsPath);
        }
    }

    /**
     * Método para extrair caminhos de arquivos .ts de um conteúdo de playlist .m3u8
     */
    private List<String> extractTsFilePathsFromPlaylist(String m3u8Playlist) {
        List<String> tsFilePaths = new ArrayList<>();
        String[] lines = m3u8Playlist.split("\n");

        for (String line : lines) {
            if (line.trim().endsWith(".ts")) {
                // Caso seja apenas o caminho simples, ex: 1080p_000.ts
                tsFilePaths.add(line.trim());
            } else if (line.trim().startsWith("http")) {
                // Caso seja um URL completo com parâmetros
                String tsFileName = line.substring(line.lastIndexOf("/") + 1); // Extraia o nome do arquivo
                if (tsFileName.contains("?")) {
                    // Remova parâmetros (tudo após '?')
                    tsFileName = tsFileName.substring(0, tsFileName.indexOf("?"));
                }
                tsFilePaths.add(tsFileName.trim()); // Adicione o nome do arquivo .ts
            }
        }

        return tsFilePaths;
    }
}


