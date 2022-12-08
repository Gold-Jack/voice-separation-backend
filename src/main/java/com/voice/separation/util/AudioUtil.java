package com.voice.separation.util;

import org.springframework.web.multipart.MultipartFile;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.sound.sampled.spi.AudioFileReader;
import java.io.IOException;

public class AudioUtil {

    public static MultipartFile toWav(MultipartFile audio) throws IOException {
        java.io.File wavFile = new java.io.File(audio.getResource().getURI());
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream((java.io.File) audio);
            AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, wavFile);
        } catch (UnsupportedAudioFileException | IOException e) {
            throw new RuntimeException(e);
        }
        return (MultipartFile) wavFile;
    }
}
