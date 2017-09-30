package com.overview;

import bwapi.*;
import bwta.BWTA;
import bwta.BaseLocation;
import com.overview.managers.ResourceGatheringManager;

public class CSuiteManager extends DefaultBWListener {

   private Mirror mirror = new Mirror();
   private Game game;
   private Player self;

   private void run() {
      mirror.getModule().setEventListener(this);
      mirror.startGame();
   }

   private ResourceGatheringManager resourceGatheringManager;

   @Override
   public void onStart() {
      game = mirror.getGame();
      self = game.self();

      //Use BWTA to analyze map
      //This may take a few minutes if the map is processed first time!
      analyzeMapData();
      //      printMapData();

      resourceGatheringManager = new ResourceGatheringManager(game);


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


   private long frameNumber = 0;

   @Override
   public void onFrame() {
      //game.setTextSize(10);
      game.drawTextScreen(10,10,"Playing as " + self.getName() + " - " + self.getRace());

      StringBuilder units = new StringBuilder("My units:\n");

      for(Unit myUnit : self.getUnits()) {
         if(myUnit.getType() == UnitType.Terran_Command_Center && self.minerals() >= 50) {
            myUnit.train(UnitType.Terran_SCV);
         }
         units.append(myUnit.getType()).append(" ").append(myUnit.getTilePosition()).append("\n");
      }
      game.drawTextScreen(10,25,units.toString());
   }

   //This happens when the unit training starts not when it is actually created!
   @Override
   public void onUnitComplete(Unit unit) {
//      System.out.println("on unit create");
      if(unit.getType() != UnitType.Resource_Mineral_Field && unit.getType() != UnitType.Resource_Vespene_Geyser) {
         System.out.println("New unit discovered " + unit.getType());
      }
      if(unit.getType().isWorker() ) {
         System.out.println("calling new worker from onUnitCreate");
         resourceGatheringManager.addNewWorker(unit);
      }
   }



   public static void main(String[] args) {
      new CSuiteManager().run();
   }
}