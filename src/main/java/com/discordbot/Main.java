package com.discordbot;

import com.sedmelluq.discord.lavaplayer.player.*;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.source.http.*;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import com.sedmelluq.discord.lavaplayer.track.playback.*;
import dev.lavalink.youtube.YoutubeAudioSourceManager;
import dev.lavalink.youtube.http.YoutubeOauth2Handler;
import org.javacord.api.*;
import java.awt.*;
import java.io.*;
import java.sql.Time;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.javacord.api.audio.*;
import org.javacord.api.entity.channel.ServerVoiceChannel;
import org.javacord.api.entity.intent.Intent;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.interaction.*;


public class Main {
    public static void main(String[] args) {
        // reading token
        String token, token2;
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream("token.txt")))) {
            token = br.readLine();
            token2 = br.readLine();
            System.out.println("I found file.");
        } catch (FileNotFoundException e) {
            System.out.println("File not found. Expected \"token.txt\"");
            throw new RuntimeException(e);
        } catch (IOException e) {
            System.out.println("Failed to read file.");
            throw new RuntimeException(e);
        }
        DiscordApi api = new DiscordApiBuilder().setToken(token).login().join();
        System.out.println(token + "\n" + token2);
        createCommands(api);
        createSlashCommandListeners(api, token2);
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

    public static void startPlaza(AudioConnection audioConnection, DiscordApi api) {
        System.out.println("player started");
        // Create a player manager
// Create a player manager
        AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
        playerManager.registerSourceManager(new HttpAudioSourceManager());
        AudioPlayer player = playerManager.createPlayer();
// Create an audio source and add it to the audio connection's queue
        AudioSource source = new LavaplayerAudioSource(api, player);
        audioConnection.setAudioSource(source);
// You can now use the AudioPlayer like you would normally do with Lavaplayer, e.g.,//https://www.youtube.com/watch?v=mXHKjFKBC0g
        System.out.println("test");//
        playerManager.loadItem("http://radio.plaza.one/ogg", new AudioLoadResultHandler() {
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

    public static void startGensokyoRadio(AudioConnection audioConnection, DiscordApi api) {
        System.out.println("player started");
        // Create a player manager
// Create a player manager
        AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
        playerManager.registerSourceManager(new HttpAudioSourceManager());
        AudioPlayer player = playerManager.createPlayer();
// Create an audio source and add it to the audio connection's queue
        AudioSource source = new LavaplayerAudioSource(api, player);
        audioConnection.setAudioSource(source);
// You can now use the AudioPlayer like you would normally do with Lavaplayer, e.g.,//https://www.youtube.com/watch?v=mXHKjFKBC0g
        System.out.println("test");//
        playerManager.loadItem("https://stream.gensokyoradio.net/1", new AudioLoadResultHandler() {
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

    public static void startYoutube(AudioConnection audioConnection, DiscordApi api, String link, SlashCommandInteraction slashCommandInteraction, boolean random, String token2) throws InterruptedException {
        AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
        YoutubeAudioSourceManager ytSourceManager = new dev.lavalink.youtube.YoutubeAudioSourceManager();
        Logger logger = Logger.getLogger(ytSourceManager.getClass().getName());
        logger.setLevel(Level.INFO);
        logger.log(Level.INFO, "TEST");
        ytSourceManager.useOauth2(token2,true);
        //ytSourceManager.useOauth2(null,false);
        //TimeUnit.SECONDS.sleep(600);
        playerManager.registerSourceManager(ytSourceManager);
        AudioSourceManagers.registerRemoteSources(playerManager,
                com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager.class);

        //AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
        //AudioSourceManagers.registerRemoteSources(playerManager);
        AudioPlayer player = playerManager.createPlayer();
        AudioSource source = new LavaplayerAudioSource(api, player);
        audioConnection.setAudioSource(source);
// You can now use the AudioPlayer like you would normally do with Lavaplayer, e.g.,//https://www.youtube.com/watch?v=mXHKjFKBC0g
        TrackScheduler trackScheduler = new TrackScheduler(player);
        player.addListener(trackScheduler);
        if (!random) {
            playerManager.loadItem(link, new AudioLoadResultHandler() {
                        @Override
                        public void trackLoaded(AudioTrack track) {
                            System.out.println("trackLoaded");
                            trackScheduler.writeList(List.of(track));
                            slashCommandInteraction.createFollowupMessageBuilder()
                                    .setContent("Loading track: " + link)
                                    .send();
//                        trackScheduler.queue(track);
                            //player.playTrack(track);

                        }

                        @Override
                        public void playlistLoaded(AudioPlaylist playlist) {
                            slashCommandInteraction.createFollowupMessageBuilder()
                                    .setContent("You are loading entire playlist. If you wish to load a single song, use URI without playlist.\nLoading playlist: " + link)
                                    .send();
//                        for (AudioTrack track : playlist.getTracks()) {
//                            trackScheduler.queue(track);
//                        }
                            trackScheduler.writeList(playlist.getTracks());
                        }

                        @Override
                        public void noMatches() {
                            // Notify the user that we've got nothing
                            System.out.println("noMatches");
                            slashCommandInteraction.createFollowupMessageBuilder()
                                    .setContent("Can't find track. " + link)
                                    .send();
                        }

                        @Override
                        public void loadFailed(FriendlyException throwable) {
                            // Notify the user that everything exploded
                            System.out.println("loadFailed");
                            slashCommandInteraction.createFollowupMessageBuilder()
                                    .setContent("Load failed. " + link)
                                    .send();
                            throw throwable;
                        }
                    }
            );
        } else {
            playerManager.loadItem(link, new AudioLoadResultHandler() {
                        @Override
                        public void trackLoaded(AudioTrack track) {
                            System.out.println("trackLoaded");
                            trackScheduler.writeList(List.of(track));
                            slashCommandInteraction.createFollowupMessageBuilder()
                                    .setContent("Loading track: " + link)
                                    .send();
//                        trackScheduler.queue(track);
                            //player.playTrack(track);

                        }

                        @Override
                        public void playlistLoaded(AudioPlaylist playlist) {
                            slashCommandInteraction.createFollowupMessageBuilder()
                                    .setContent("You are loading entire playlist. If you wish to load a single song, use URI without playlist.\nLoading playlist: " + link)
                                    .send();
//                        for (AudioTrack track : playlist.getTracks()) {
//                            trackScheduler.queue(track);
//                        }
                            trackScheduler.randomList(playlist.getTracks());
                        }

                        @Override
                        public void noMatches() {
                            // Notify the user that we've got nothing
                            System.out.println("noMatches");
                            slashCommandInteraction.createFollowupMessageBuilder()
                                    .setContent("Can't find track. " + link)
                                    .send();
                        }

                        @Override
                        public void loadFailed(FriendlyException throwable) {
                            // Notify the user that everything exploded
                            System.out.println("loadFailed");
                            slashCommandInteraction.createFollowupMessageBuilder()
                                    .setContent("Load failed. " + link)
                                    .send();
                            throw throwable;
                        }
                    }
            );
        }
    }

    public static void createCommands(DiscordApi api) {

        // Slash command for plaza
        SlashCommand command2 = SlashCommand.with("plaza", "Connect to VC and start playing plaza.one")
                .createGlobal(api)
                .join();
        // Slash command for gensokyoRadio
        SlashCommand command22 = SlashCommand.with("gensokyoradio", "Connect to VC and start playing gensokyoradio")
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
        SlashCommand command6 = SlashCommand.with("play", "Try playing youtube",Arrays.asList(
                    SlashCommandOption.createStringOption("Link","https://www.youtube.com/watch?v=mXHKjFKBC0g",true),
                    SlashCommandOption.createBooleanOption("Random","Shuffle the playlist?",false)
                    //SlashCommandOption.createBooleanOption("Random?","Shuffle playlist?",false)
                ))
//                .addOption(SlashCommandOption.createStringOption("Youtube Link","https://www.youtube.com/watch?v=mXHKjFKBC0g",false)
                .createGlobal(api)
                .join();
    }

    public static void createSlashCommandListeners(DiscordApi api, String token2) {
        api.addSlashCommandCreateListener(event -> {
            SlashCommandInteraction slashCommandInteraction = event.getSlashCommandInteraction();

            //plaza
                if (Objects.equals(slashCommandInteraction.getCommandName(), "plaza")) {
                    ServerVoiceChannel channel = slashCommandInteraction.getUser().getConnectedVoiceChannel(slashCommandInteraction.getServer().get()).get();
                    try {
                        AudioConnection audioConnection = channel.connect().get(10,TimeUnit.SECONDS);
                        System.out.println("start");
                        startPlaza(audioConnection,api);
                        slashCommandInteraction.createImmediateResponder().setContent("OK.").respond();
                    } catch (InterruptedException | ExecutionException | TimeoutException e) {
                        throw new RuntimeException(e);
                    }
                    //gensokyoradio
            } else if (Objects.equals(slashCommandInteraction.getCommandName(), "gensokyoradio")) {
                    ServerVoiceChannel channel = slashCommandInteraction.getUser().getConnectedVoiceChannel(slashCommandInteraction.getServer().get()).get();
                    try {
                        AudioConnection audioConnection = channel.connect().get(10,TimeUnit.SECONDS);
                        System.out.println("start");
                        startGensokyoRadio(audioConnection,api);
                        slashCommandInteraction.createImmediateResponder().setContent("OK.").respond();
                    } catch (InterruptedException | ExecutionException | TimeoutException e) {
                        throw new RuntimeException(e);
                    }
                    //help
            } else if (Objects.equals(slashCommandInteraction.getCommandName(), "help")) {
                    EmbedBuilder embedBuilder = new EmbedBuilder()
                            .setTitle("Slash Commands:")
                            .addField("/help", "This.")
                            .addField("/help command:text", "Check help for specific command")
                            .addField("/plaza", "Connect to VC and start playing plaza.one")
                            .addField("/playmeltyblood", "What's Melty Blood?");
                    slashCommandInteraction.createImmediateResponder().addEmbed(embedBuilder).respond();

                    //playmeltyblood
            } else if (Objects.equals(slashCommandInteraction.getCommandName(), "playmeltyblood")) {
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

                //stop
            } else if (Objects.equals(slashCommandInteraction.getCommandName(), "stop")) {
                if (slashCommandInteraction.getServer().isPresent()) {
                    if (slashCommandInteraction.getServer().get().getConnectedVoiceChannel(api.getYourself()).isPresent()) {
                        slashCommandInteraction
                                .getServer().get()
                                .getConnectedVoiceChannel(api.getYourself()).get()
                                .disconnect();
                        slashCommandInteraction.createImmediateResponder().setContent("Bye.").respond();
                    } else slashCommandInteraction.createFollowupMessageBuilder()
                            .setContent("Voicechat not found")
                            .send();
                } else slashCommandInteraction.createFollowupMessageBuilder()
                        .setContent("Group not found")
                        .send();
                //youtube
            } else if (Objects.equals(slashCommandInteraction.getCommandName(), "play")) {
//                    slashCommandInteraction.respondLater().thenAccept(interactionOriginalResponseUpdater -> {
//                        interactionOriginalResponseUpdater.setContent("Loading...").update();
//                    });
                    slashCommandInteraction.respondLater();
                    if (slashCommandInteraction.getUser()
                            .getConnectedVoiceChannel(slashCommandInteraction.getServer().get()).isPresent()) {
                        ServerVoiceChannel channel = slashCommandInteraction.getUser()
                                .getConnectedVoiceChannel(slashCommandInteraction.getServer().get()).get();
                        try {
                            AudioConnection audioConnection = channel.connect().get(10, TimeUnit.SECONDS);
                            startYoutube(audioConnection, api,
                                    slashCommandInteraction.getArgumentStringValueByIndex(0).get(), slashCommandInteraction,
                                    slashCommandInteraction.getArgumentBooleanValueByIndex(1).orElse(false),token2);

                        } catch (InterruptedException | ExecutionException | TimeoutException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        slashCommandInteraction.createFollowupMessageBuilder()
                                .setContent("Voicechat not found. Are you in a voicechat?")
                                .send();
                    }
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
