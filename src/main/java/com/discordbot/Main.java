package com.discordbot;

import com.sedmelluq.discord.lavaplayer.player.*;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.source.youtube.*;
import com.sedmelluq.discord.lavaplayer.source.http.*;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.playback.*;
import org.javacord.api.*;
import java.awt.*;
import java.io.*;
import java.net.http.*;
import java.net.*;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.javacord.api.audio.*;
import org.javacord.api.entity.channel.ServerVoiceChannel;
import org.javacord.api.entity.message.Messageable;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.interaction.*;
import org.json.*;

public class Main {
    public static void main(String[] args) {
        String token;
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream("token.txt")))) {
            token = br.readLine();
            System.out.println("I found file.");
        } catch (FileNotFoundException e) {
            System.out.println("File not found. Expected \"token.txt\"");
            throw new RuntimeException(e);
        } catch (IOException e) {
            System.out.println("Failed to read file.");
            throw new RuntimeException(e);
        }
        DiscordApi api = new DiscordApiBuilder().setToken(token).login().join();

        createCommands(api);
        createSlashCommandListeners(api);
        // Print the invite url of your bot
        System.out.println("You can invite the bot by using the following url: " + api.createBotInvite());
        try {
            var commands = api.getGlobalSlashCommands().get();
            for (var command : commands) {
                System.out.println(command.getName()+" "+command.getId());
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public static EmbedBuilder embedWaifuPics(String link) {
        var client = HttpClient.newHttpClient();
        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setTitle("No answer.");
        var request = HttpRequest.newBuilder(
                        URI.create(link))
                .header("accept", "application/json")
                .build();
        HttpResponse<String> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            return embedBuilder;
        }
        JSONObject jsonObject = new JSONObject(response.body());
        if (jsonObject.has("message")) {
            embedBuilder = new EmbedBuilder()
                    .setTitle((String) jsonObject.get("message"));
//                    .addField("Request:",link);
            return embedBuilder;
        } else if (jsonObject.has("url")) {
            embedBuilder = new EmbedBuilder()
                    .setImage((String) jsonObject.get("url"));
            return embedBuilder;
        } else return embedBuilder;
    }

    public static EmbedBuilder embedWaifu(String link) {
        var client = HttpClient.newHttpClient();
        EmbedBuilder embedBuilder = new EmbedBuilder()
                .addField("Whoops, nothing there","Something went wrong.");
        var request = HttpRequest.newBuilder(
                        URI.create(link))
                .header("accept", "application/json")
                .build();

        try {
            try {
                var response = client.send(request, HttpResponse.BodyHandlers.ofString());
                JSONObject jsonObject = new JSONObject(response.body());
                var images = (JSONArray) jsonObject.get("images");
                var image = (JSONObject) images.get(0);
                var url = (String) image.get("url");
                var source = "https://example.com";
                if (image.has("source")) {
                    if (image.get("source") instanceof String) {
                        source = (String) image.get("source");
                    }
                }
                embedBuilder = new EmbedBuilder()
                        .setTitle("#" + (Integer) image.get("image_id"))
                        .setImage(url);
                if (!source.equals("https://example.com")) embedBuilder.setAuthor("Source", source, "https://emojipedia-us.s3.dualstack.us-west-1.amazonaws.com/thumbs/240/microsoft/310/red-heart_2764-fe0f.png");
                return embedBuilder;
            } catch (IOException | InterruptedException e) {
                return embedBuilder;
            }
        } catch (ClassCastException e) {
            return embedBuilder;
        }
    }

    public static void startPlayer(AudioConnection audioConnection, DiscordApi api) {
        System.out.println("player started");
        // Create a player manager
// Create a player manager
        AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
        playerManager.registerSourceManager(new HttpAudioSourceManager());
        AudioPlayer player = playerManager.createPlayer();
// Create an audio source and add it to the audio connection's queue
        AudioSource source = new LavaplayerAudioSource(api, player);
        audioConnection.setAudioSource(source);
// You can now use the AudioPlayer like you would normally do with Lavaplayer, e.g.,
        System.out.println();
        playerManager.loadItem("http://radio.plaza.one/mp3", new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                System.out.println("trackLoaded");
                player.playTrack(track);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                System.out.println("playlistLoaded");
                for (AudioTrack track : playlist.getTracks()) {
                    player.playTrack(track);
                }
            }

            @Override
            public void noMatches() {
                // Notify the user that we've got nothing
                System.out.println("noMatches");
            }

            @Override
            public void loadFailed(FriendlyException throwable) {
                // Notify the user that everything exploded
                System.out.println("loadFailed");

            }
        }
        );
    }

    public static void createCommands(DiscordApi api) {
        // Slash command for plaza
        SlashCommand command2 = SlashCommand.with("plaza", "Connect to VC and start playing plaza.one")
                .createGlobal(api)
                .join();
        // Slash command for help
        SlashCommand command3 = SlashCommand.with("help", "Display help.",Arrays.asList(
                SlashCommandOption.createStringOption("command","Get Help for specific slash command",false)))
                .createGlobal(api)
                .join();
        // Slash command for playmeltyblood
        SlashCommand command4 = SlashCommand.with("playmeltyblood", "You should play melty blood right now!")
                .createGlobal(api)
                .join();
        SlashCommand command5679 = SlashCommand.with("stop", "Stop playing radio (WIP)")
                .createGlobal(api)
                .join();
    }

    public static void createSlashCommandListeners(DiscordApi api) {
        api.addSlashCommandCreateListener(event -> {
            SlashCommandInteraction slashCommandInteraction = event.getSlashCommandInteraction();
                if (Objects.equals(slashCommandInteraction.getCommandName(), "plaza")) {
                //plaza Command listener
                if (slashCommandInteraction.getUser().getConnectedVoiceChannel(slashCommandInteraction.getServer().get()).isPresent()) {
                    ServerVoiceChannel channel = slashCommandInteraction.getUser().getConnectedVoiceChannel(slashCommandInteraction.getServer().get()).get();
                    channel.connect().thenAccept(audioConnection -> {
                        System.out.println("startPlayerBegin");
                        startPlayer(audioConnection, api);
                        System.out.println("startPlayerStop");
                    }).exceptionally(e -> {
                        System.out.println("Failed to connect to voice channel (no permissions?)");
                        e.printStackTrace();
                        return null;
                    });
                } else slashCommandInteraction.createImmediateResponder().setContent("Join VC first.").respond();

            } else if (Objects.equals(slashCommandInteraction.getCommandName(), "help")) {
                //help Command listener
                    EmbedBuilder embedBuilder = new EmbedBuilder()
                            .setTitle("Slash Commands:")
                            .addField("/help", "This.")
                            .addField("/help command:text", "Check help for specific command")
                            .addField("/plaza", "Connect to VC and start playing plaza.one")
                            .addField("/waifu nsfw:false gif:false", "Random picture from waifu.moe.")
                            .addField("/waifupics nsfw:false tag:text", "Random picture from waifu.pics.")
                            .addField("/colors", "Random colorpalette from colourlovers.com")
                            .addField("/playmeltyblood", "What's Melty Blood?")
                            .setFooter("try /help command:waifupics");
                    slashCommandInteraction.createImmediateResponder().addEmbed(embedBuilder).respond();
            } else if (Objects.equals(slashCommandInteraction.getCommandName(), "playmeltyblood")) {
                //playmeltyblood Command listener
                EmbedBuilder embedBuilder = new EmbedBuilder()
                        .setTitle("You should play Melty Blood Actress Again Current Code right now.")
                        .setColor(Color.red)
                        .setUrl("https://play.meltyblood.club/")
                        .setImage("https://wiki.gbl.gg/images/thumb/4/41/MBAACC_Logo.png/400px-MBAACC_Logo.png")
                        .addField("About Melty"," Melty Blood is a fighting game developed by French Bread." +
                                " It is based on Type Moon's hit visual novel Tsukihime." +
                                " Since release at Comiket in December 2002 there were 4 major updates." +
                                " The current version being Melty Blood Actress Again Current Code Ver. 1.07." +
                                " With a cast of 31 characters with 3 forms (Moons) each the game has a total of 93 playable characters. ")
                        .addField("Why should I play Melty Blood?","It's free," +
                                " it runs on any hardware, it is an old anime fighting game that will never be created ever again.")
                        .setFooter("play.meltyblood.com")
                        .setUrl("https://play.meltyblood.club/");
                slashCommandInteraction.createImmediateResponder().addEmbed(embedBuilder).respond();
            } else if (Objects.equals(slashCommandInteraction.getCommandName(), "stop")) {
                if (slashCommandInteraction.getServer().isPresent()) {
                    if (slashCommandInteraction.getServer().get().getConnectedVoiceChannel(api.getYourself()).isPresent()) {
                        slashCommandInteraction
                                .getServer().get()
                                .getConnectedVoiceChannel(api.getYourself()).get()
                                .disconnect();
                        slashCommandInteraction.createImmediateResponder().setContent("OK.").respond();
                    } else slashCommandInteraction.createImmediateResponder().setContent("Voice channel not found.").respond();
                } else slashCommandInteraction.createImmediateResponder().setContent("No group found.").respond();
            }
        });
    }
 }
// LavaPlayerAudioSource
    class LavaplayerAudioSource extends AudioSourceBase {

    private final AudioPlayer audioPlayer;
    private AudioFrame lastFrame;

    /**
     * Creates a new lavaplayer audio source.
     *
     * @param api A discord api instance.
     * @param audioPlayer An audio player from Lavaplayer.
     */
    public LavaplayerAudioSource(DiscordApi api, AudioPlayer audioPlayer) {
        super(api);
        this.audioPlayer = audioPlayer;
    }

    @Override
    public byte[] getNextFrame() {
        if (lastFrame == null) {
            return null;
        }
        return applyTransformers(lastFrame.getData());
    }

    @Override
    public boolean hasFinished() {
        return false;
    }

    @Override
    public boolean hasNextFrame() {
        lastFrame = audioPlayer.provide();
        return lastFrame != null;
    }

    @Override
    public AudioSource copy() {
        return new LavaplayerAudioSource(getApi(), audioPlayer);
    }
}