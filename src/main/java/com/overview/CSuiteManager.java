package com.overview;

import bwapi.*;
import bwta.BWTA;
import bwta.BaseLocation;
import com.overview.managers.WorkerManager;

public class CSuiteManager extends DefaultBWListener {

   private Mirror mirror = new Mirror();
   private Game game;
   private Player self;

   public void run() {
      mirror.getModule().setEventListener(this);
      mirror.startGame();
   }

   WorkerManager workerManager;

   @Override
   public void onStart() {
      game = mirror.getGame();
      self = game.self();


      //Use BWTA to analyze map
      //This may take a few minutes if the map is processed first time!
      analyzeMapData();
//      printMapData();


      workerManager = new WorkerManager(game);


   }

   private void printMapData() {
      int i = 0;
      for(BaseLocation baseLocation : BWTA.getBaseLocations()) {
         System.out.println("Base location #" + (++i) + ". Printing location's region polygon:");
         for(Position position : baseLocation.getRegion().getPolygon().getPoints()) {
            System.out.print(position + ", ");
         }
         System.out.println();
      }
   }

   private void analyzeMapData() {
      System.out.println("Analyzing map...");
      BWTA.readMap();
      BWTA.analyze();
      System.out.println("Map data ready");
   }

   @Override
   public void onFrame() {
      //game.setTextSize(10);
      game.drawTextScreen(10,10,"Playing as " + self.getName() + " - " + self.getRace());

      StringBuilder units = new StringBuilder("My units:\n");

      //iterate through my units
      for(Unit myUnit : self.getUnits()) {
         units.append(myUnit.getType()).append(" ").append(myUnit.getTilePosition()).append("\n");

         //if there's enough minerals, train an SCV
         if(myUnit.getType() == UnitType.Terran_Command_Center && self.minerals() >= 50) {
            myUnit.train(UnitType.Terran_SCV);
         }

         //Display the location of all your workers.
//         if(myUnit.getType().isWorker() && myUnit.isIdle()) {
//            game.drawTextMap(myUnit.getPosition().getX(),myUnit.getPosition().getY(),
//                  "TilePos: " + myUnit.getTilePosition().toString() + " Pos: " + myUnit.getPosition().toString());
//         }
      }

      //draw my units on screen
      game.drawTextScreen(10,25,units.toString());
   }

   @Override
   public void onUnitCreate(Unit unit) {
      if(unit.getType() != UnitType.Resource_Mineral_Field && unit.getType() != UnitType.Resource_Vespene_Geyser) {
         System.out.println("New unit discovered " + unit.getType());
      }
      if(unit.getType().isWorker()) {
         workerManager.addNewWorker(unit);
      }
   }

   public static void main(String[] args) {
      new CSuiteManager().run();
   }
}