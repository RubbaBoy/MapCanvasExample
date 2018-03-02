package com.uddernetworks.mapcanvas.example;

import com.uddernetworks.mapcanvas.api.MapCanvas;
import com.uddernetworks.mapcanvas.api.MapCanvasAPI;
import com.uddernetworks.mapcanvas.api.MapWall;
import com.uddernetworks.mapcanvas.api.WallDirection;
import com.uddernetworks.mapcanvas.api.event.ClickMapEvent;
import com.uddernetworks.mapcanvas.api.font.MinecraftFont;
import com.uddernetworks.mapcanvas.api.objects.*;
import com.uddernetworks.mapcanvas.api.objects.Image;
import com.uddernetworks.mapcanvas.api.objects.Rectangle;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Rotation;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.map.MapPalette;
import org.bukkit.plugin.java.JavaPlugin;

import java.awt.*;

public class Example extends JavaPlugin implements Listener {

    private int startingMapID = 0;
    private int width = 0;
    private int height = 0;

    private MapCanvasAPI mapCanvasAPI;

    @Override
    public void onEnable() {
        Bukkit.getServer().getPluginManager().registerEvents(this, this);

        initializeCanvasAPI();
    }

    private void initializeCanvasAPI() {
        if (Bukkit.getPluginManager().isPluginEnabled("MapCanvasAPI")) {
            this.mapCanvasAPI = (MapCanvasAPI) Bukkit.getPluginManager().getPlugin("MapCanvasAPI");
            return;
        }

        getLogger().severe("MapCanvasAPI not found!");
        this.getPluginLoader().disablePlugin(this);
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("canvas")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("Players only, dude.");
                return true;
            }

            Player player = (Player) sender;

            if (args.length == 0) {
                player.sendMessage(ChatColor.RED + "Yeah, no");
                return true;
            }

            if (args[0].equalsIgnoreCase("create")) {
                if (args.length != 3) {
                    player.sendMessage(ChatColor.RED + "Yeah, no, dude. Usage: /canvas create [width] [height]");
                    return true;
                }

                this.width = Integer.valueOf(args[1]);
                this.height = Integer.valueOf(args[2]);
                this.startingMapID = 0;

                new MapWall(player.getLocation(), WallDirection.Z_AXIS, BlockFace.EAST, Material.WOOL, this.width, this.height, (short) 0);

                player.sendMessage(ChatColor.GOLD + "Width: " + this.width + " Height: " + this.height + " Total maps: " + (this.width * this.height) + "Starting ID: " + this.startingMapID);
            } else if (args[0].equalsIgnoreCase("use")) {
                if (args.length != 4) {
                    player.sendMessage(ChatColor.RED + "Yeah, no, dude. Usage: /canvas use [width] [height] [startingID]");
                    return true;
                }

                this.width = Integer.valueOf(args[1]);
                this.height = Integer.valueOf(args[2]);
                this.startingMapID = Integer.valueOf(args[3]);

                player.getWorld().getEntities().stream().filter(entity -> entity instanceof ItemFrame).map(ItemFrame.class::cast).forEach(itemFrame -> itemFrame.setRotation(Rotation.CLOCKWISE_45));

                player.sendMessage(ChatColor.GOLD + "Width: " + this.width + " Height: " + this.height + " Starting ID: " + this.startingMapID);
            } else if (args[0].equalsIgnoreCase("draw")) {
                player.sendMessage(ChatColor.GOLD + "Starting drawing...");

                MapCanvas mapCanvas = new MapCanvas(this, mapCanvasAPI, WallDirection.Z_AXIS, BlockFace.EAST, width, height, startingMapID);

                Rectangle rectangle = new Rectangle(256, 0, 1024, 1024, (byte) -1, MapPalette.matchColor(Color.GREEN));
                rectangle.setClick((clickingPlayer, action, mapCanvasSection, x, y) -> clickingPlayer.sendMessage(ChatColor.GREEN + "You clicked the green rectangle, action = " + action + "specifically at (" + x + ", " + y + ")"));

                mapCanvas.addObject(rectangle);

                mapCanvas.addObject(new Circle(0, 0, 10, 15, MapPalette.matchColor(Color.RED))); // Top left
                mapCanvas.addObject(new Circle(width * 128, 0, 10, 15, MapPalette.matchColor(Color.GREEN))); // Top right
                mapCanvas.addObject(new Circle(0, height * 128, 10, 15, MapPalette.matchColor(Color.BLUE))); // Bottom left
                mapCanvas.addObject(new Circle(width * 128, height * 128, 10, 15, MapPalette.matchColor(Color.YELLOW))); // Bottom right

                int imageWidth = 640;
                int imageHeight = 480;

                com.uddernetworks.mapcanvas.api.objects.Image image = new Image("https://rubbaboy.me/images/zvke6pw.png", (width * 128 - imageWidth) / 2, (height * 128 - imageHeight) / 2, imageWidth, imageHeight);
                image.setClick((clickingPlayer, action, mapCanvasSection, x, y) -> clickingPlayer.sendMessage(ChatColor.BLUE + "Clicked image, action = " + action + " specifically at (" + x + ", " + y + ")"));

                mapCanvas.addObject(image);

                mapCanvas.addObject(new Text(128, 128, new MinecraftFont(32), MapPalette.matchColor(Color.MAGENTA), "Example Text!"));

                mapCanvas.addObject(new ItemRender(mapCanvasAPI, Material.STONE, 512, 512 + 500, 256, 256));
                mapCanvas.addObject(new ItemRender(mapCanvasAPI, Material.DIAMOND_SWORD, 1000, 1500, 512, 512));
                mapCanvas.addObject(new ItemRender(mapCanvasAPI, Material.GOLD_INGOT, 500, 1500, 128 * 3, 128 * 3));

                mapCanvas.initialize(player.getWorld());

                mapCanvas.paint();

                player.sendMessage(ChatColor.GOLD + "Finished drawing on the map: " + mapCanvas.getUUID());
            }
        }

        return true;
    }

    @EventHandler
    public void onClickMapEvent(ClickMapEvent event) {
        MapCanvas mapCanvas = event.getMapCanvas();

        event.getPlayer().sendMessage("Clicked at (" + event.getX() + ", " + event.getY() + ") on map: " + mapCanvas.getUUID());

        Circle circle = new Circle(event.getX(), event.getY(), 50, 70, MapPalette.matchColor(Color.RED));

        mapCanvas.addObject(circle);

        circle.draw(mapCanvas);

        mapCanvas.updateMaps();
    }
}
