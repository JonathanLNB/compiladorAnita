package Herramientas;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class AccesoCodigo extends File {
    private BufferedReader br;
    private String textoIn;

    public AccesoCodigo(String textoIn) {
        super(textoIn);
        this.textoIn = textoIn;
    }

    public String getCodigo(int linea) {
        String fila = "";
        try {
            br = new BufferedReader(new FileReader(textoIn));
            for (int i = 0; i <= linea; i++) {
                fila += br.readLine() + "\n";
            }
            br.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } finally {
            return fila;
        }
    }
}
