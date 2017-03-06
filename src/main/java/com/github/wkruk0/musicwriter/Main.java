package com.github.wkruk0.musicwriter;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class Main {

  public static String GREETING = "Hello world!";

  public static void main(String[] args) {
    System.out.println(GREETING);
    if (args.length < 1) {
      System.err.println("You must provide a path to mp3 file.");
      System.exit(-1);
    }
    System.out.println("Reading mp3 file from path: " + args[0]);
    new Main().testPlay(args[0]);
  }

  public void testPlay(String filename) {
    try {
      File file = new File(filename);
      AudioInputStream in = AudioSystem.getAudioInputStream(file);
      AudioInputStream din = null;
      AudioFormat baseFormat = in.getFormat();
      AudioFormat decodedFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
          baseFormat.getSampleRate(),
          16,
          baseFormat.getChannels(),
          baseFormat.getChannels() * 2,
          baseFormat.getSampleRate(),
          false);
      din = AudioSystem.getAudioInputStream(decodedFormat, in);
      // Play now.
      rawplay(decodedFormat, din);
      in.close();
    } catch (Exception e) {
      System.err.print(e.getMessage());
      //Handle exception.
    }
  }

  private void rawplay(AudioFormat targetFormat, AudioInputStream din) throws IOException, LineUnavailableException {
    byte[] data = new byte[4096];
    SourceDataLine line = getLine(targetFormat);
    if (line != null) {
      // Start
      line.start();
      int nBytesRead = 0, nBytesWritten = 0;
      while (nBytesRead != -1) {
        nBytesRead = din.read(data, 0, data.length);
        if (nBytesRead != -1) nBytesWritten = line.write(data, 0, nBytesRead);
      }
      // Stop
      line.drain();
      line.stop();
      line.close();
      din.close();
    }
  }

  private SourceDataLine getLine(AudioFormat audioFormat) throws LineUnavailableException {
    SourceDataLine res = null;
    DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
    res = (SourceDataLine) AudioSystem.getLine(info);
    res.open(audioFormat);
    return res;
  }
}

