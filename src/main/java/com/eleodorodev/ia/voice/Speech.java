package com.eleodorodev.ia.voice;

import com.eleodorodev.ia.config.Config;
import com.eleodorodev.ia.config.Config.Log;
import com.eleodorodev.ia.exception.IAException;
import com.google.cloud.speech.v1.*;
import com.google.cloud.texttospeech.v1.*;
import com.google.protobuf.ByteString;

import javax.sound.sampled.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Speech {
    private boolean endHear;

    public void setEndHear(boolean endHear) {
        this.endHear = endHear;
    }

    public String hear (){
        Log.LOGGER.info("Conversa inicializada");
        AudioFormat format = new AudioFormat(16000, 16, 1, true, true);
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
        if (!AudioSystem.isLineSupported(info)) {
            Log.LOGGER.severe("O line não é suportado.");
            System.exit(0);
        }
        try {
            TargetDataLine line = (TargetDataLine) AudioSystem.getLine(info);
            line.open(format);
            line.start();

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            byte[] buffer = new byte[4096];
            int bytesRead;

            do {
                bytesRead = line.read(buffer, 0, buffer.length);
                outputStream.write(buffer, 0, bytesRead);
                Log.LOGGER.info(String.format("[%s] - Ouvindo usuario", new SimpleDateFormat("dd-MM-yyyy").format(new Date())));
            } while (!endHear);

            line.stop();
            line.close();

            byte[] audioData = outputStream.toByteArray();
            AudioInputStream audioInputStream = new AudioInputStream(new ByteArrayInputStream(audioData), format, audioData.length);
            File outputFile = new File(Config.AUDIO_PATCH);
            AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, outputFile);
            return toText();
        } catch (LineUnavailableException | IOException | IAException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private String toText() throws IOException, IAException {
        StringBuilder textResult = new StringBuilder();
        byte[] audioBytes = Files.readAllBytes(Paths.get(Config.AUDIO_PATCH));
        ByteString audioData = ByteString.copyFrom(audioBytes);

        RecognitionConfig config = RecognitionConfig.newBuilder()
                .setEncoding(RecognitionConfig.AudioEncoding.LINEAR16)
                .setSampleRateHertz(16000)
                .setLanguageCode("pt-BR")
                .build();

        RecognitionAudio audio = RecognitionAudio.newBuilder()
                .setContent(audioData)
                .build();

        try (SpeechClient client = SpeechClient.create()) {
            RecognizeResponse response = client.recognize(config, audio);

            List<SpeechRecognitionResult> results = response.getResultsList();
            for (SpeechRecognitionResult result : results) {
                List<SpeechRecognitionAlternative> alternatives = result.getAlternativesList();
                for (SpeechRecognitionAlternative alternative : alternatives) {
                    textResult.append(alternative.getTranscript());
                }
            }

        }
        audioDelete();
        Log.LOGGER.info("[Voce] - " + textResult);
        return textResult.toString();
    }

    public void toSpeak(String text) {
        try(TextToSpeechClient client = TextToSpeechClient.create()){
            SynthesisInput input = SynthesisInput.newBuilder()
                    .setText(text)
                    .build();

            VoiceSelectionParams voice = VoiceSelectionParams
                    .newBuilder()
                    .setLanguageCode("pt-BR")
                    .setName("pt-BR-Neural2-B")
                    .setSsmlGender(SsmlVoiceGender.SSML_VOICE_GENDER_UNSPECIFIED)
                    .build();

            AudioConfig config = AudioConfig.newBuilder()
                    .setAudioEncoding(AudioEncoding.LINEAR16)
                    .build();

            SynthesizeSpeechResponse voiceResponse = client.synthesizeSpeech(input,voice,config);
            voiceResponse.getAudioContent().writeTo(new FileOutputStream(Config.AUDIO_PATCH));
            speak();
        }catch (Exception e){
            Log.LOGGER.severe(e.getMessage());
        }
    }

    private void speak() throws IAException {
        try (AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(Config.AUDIO_PATCH))){
            AudioFormat audioFormat = audioInputStream.getFormat();
            DataLine.Info info = new DataLine.Info(SourceDataLine.class,audioFormat);
            SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);

            line.open(audioFormat);
            line.start();

            byte[] buffer = new byte[4096];
            int read;
            while ((read = audioInputStream.read(buffer)) != -1){
                line.write(buffer,0,read);
            }
            line.drain();
            line.close();
            audioDelete();
        } catch (IOException | UnsupportedAudioFileException | LineUnavailableException e) {
            throw new IAException(e);
        }
    }


    private void audioDelete() throws IAException {
        try {
            Files.delete(Path.of(Config.AUDIO_PATCH));
        }catch (Exception e){
            throw new IAException(e);
        }
    }

    public boolean isEndHear() {
        return endHear;
    }
}
