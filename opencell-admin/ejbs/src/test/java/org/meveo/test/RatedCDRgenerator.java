package org.meveo.test;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Random;

public class RatedCDRgenerator {

    String fileName;
    long nbRecords, shift;
    long startTime;

    public RatedCDRgenerator(String fileName, long nbRecords, long shift, long time) {
        this.fileName = fileName;
        this.nbRecords = nbRecords;
        this.shift = shift;
        this.startTime = time;
    }

    public static void main(String[] args) {
        PrintWriter out = null;
        try {
            out = new PrintWriter(new FileOutputStream("c:\\tmp\\matooma100K.csv"));
            StringBuffer sb = new StringBuffer();
            for (long i = 0; i < 500; i++) {
                Random r = new Random();
                for (int z = 0; z < 3; z++) {
                    String line = "2019-05-" + String.format("%02d", r.nextInt(29) + 1) + "T" + String.format("%02d", r.nextInt(23)) + ":" + String.format("%02d", r.nextInt(59))
                            + ":" + String.format("%02d", r.nextInt(59)) + ".546Z;49;069342392900" + (3000 + i) + ";VOICE_INCOMING;069342392900" + (3000 + i) + ";9525;"
                            + (new Double(r.nextDouble()).toString().substring(0, 4));
                    out.println(line);
                }
                
            }
            for (long i = 0; i < 1000; i++) {
                Random r = new Random();
                for (int z = 0; z < 3; z++) {
                    String line = "2019-05-" + String.format("%02d", r.nextInt(29) + 1) + "T" + String.format("%02d", r.nextInt(23)) + ":" + String.format("%02d", r.nextInt(59))
                            + ":" + String.format("%02d", r.nextInt(59)) + ".546Z;49;069342392912" + (3000 + i) + ";VOICE_INCOMING;069342392912" + (3000 + i) + ";9525;"
                            + (new Double(r.nextDouble()).toString().substring(0, 4));
                    out.println(line);
                }
                
            }

            for (long i = 0; i < 2000; i++) {
                Random r = new Random();
                for (int z = 0; z < 3; z++) {
                    String line = "2019-05-" + String.format("%02d", r.nextInt(29) + 1) + "T" + String.format("%02d", r.nextInt(23)) + ":" + String.format("%02d", r.nextInt(59))
                            + ":" + String.format("%02d", r.nextInt(59)) + ".546Z;49;069342392912" + (30000 + i) + ";VOICE_INCOMING;069342392912" + (30000 + i) + ";9525;"
                            + (new Double(r.nextDouble()).toString().substring(0, 4));
                    out.println(line);
                }
                
            }
            
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                out.close();
            }
        }

    }

}
