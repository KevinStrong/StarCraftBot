package com.overview.managers;

import bwapi.Game;
import bwapi.Position;
import bwapi.Unit;
import bwapi.UnitType;
import bwta.BWTA;
import bwta.BaseLocation;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class ResourceGatheringManager {

   Queue<BaseLocation> baseLocations = new LinkedList<>(BWTA.getBaseLocations());
   private List<Unit> gasWorkers;
   private List<Unit> mineralWorkers;
   private List<Unit> scoutingWorkers;
   private List<Unit> minerals;
   private Game theGame;
   //Somehow the WorkerManager needs to do the crucial task of balance gas and mineral production
   private Unit base;
   private int workerTicker = 0;

   public ResourceGatheringManager(Game aGame) {
      theGame = aGame;
      gasWorkers = new ArrayList<>();
      mineralWorkers = new ArrayList<>();
      scoutingWorkers = new ArrayList<>();
      minerals = new ArrayList<>();
      aGame.self().getUnits().forEach(this::addNewBase);
      aGame.neutral().getUnits().forEach(this::addNewMineralPatch);
      System.out.println("Finished creating worker manager");
   }

   private void addNewBase(Unit aUnit) {
      if(aUnit.getType() == UnitType.Terran_Command_Center) {
         base = aUnit;
      }
   }


   public void addNewMineralPatch(Unit aUnit) {
      if(minerals.contains(aUnit)) {
         return;
      }
//      System.out.println( "Unit type:" + aUnit.getType() + " and distance: " + aUnit.getDistance(base));
      if(aUnit.getType().isMineralField() && aUnit.getDistance(base) < 700) {
         System.out.println("Adding new mineral patch");
         minerals.add(aUnit);
      }
   }

   public void addNewWorker(Unit aUnit) {
      if(mineralWorkers.contains(aUnit) || scoutingWorkers.contains(aUnit) || gasWorkers.contains(aUnit)) {
         return;
      }
      System.out.println("Adding new worker");
      if(aUnit.isIdle() && aUnit.getType().isWorker()) {
         if(shouldCollectGas()) {
            addNewGaser(aUnit);
         }else if(shouldCollectMinerals()) {
            addNewMiner(aUnit);
         }else {
            addNewScouter(aUnit);
         }
      }
   }

   private boolean shouldCollectMinerals() {
      return workerTicker++ % 5 != 0;
   }

   private boolean shouldCollectGas() {
      return false;
   }

   private int whichMineralToUse = 0;
   private int numberOfMiners = 0;
   private void addNewMiner(Unit aNewWorker) {
      System.out.println("Number of detected minerals" + minerals.size());
      Unit closestMineral = minerals.get(whichMineralToUse);
      whichMineralToUse = (whichMineralToUse >= minerals.size()) ? 0 : whichMineralToUse + 1;

      if(closestMineral != null) {
         System.out.println("Gather minerals: " + ++numberOfMiners);
         aNewWorker.gather(closestMineral);
      }
   }

   private void addNewGaser(Unit aUnit) {

   }

   private void addNewScouter(Unit aUnit) {
      System.out.println("Having worker scout");
      aUnit.move(getNextBaseLocation());
   }

   public Position getNextBaseLocation() {
      BaseLocation remove = baseLocations.remove();
      //      while(remove != null && remove.getPosition().equals(base.get(0).getPosition())
      //            && !remove.isStartLocation()) {
      //         System.out.println("Getting new base to scout");
      //         remove = baseLocations.remove();
      //      }
      return remove.getPosition();
   }
}
