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

public class WorkerManager {

   Queue<BaseLocation> baseLocations = new LinkedList<>(BWTA.getBaseLocations());
   private List<Unit> workers;
   private List<Unit> gasWorkers;
   private List<Unit> mineralWorkers;
   private List<Unit> scoutingWorkers;
   private Game theGame;
   //Somehow the WorkerManager needs to do the crucial task of balance gas and mineral production
   private List<Unit> bases;
   private int workerTicker = 0;

   public WorkerManager(Game aGame) {
      theGame = aGame;
      workers = new ArrayList<>();
      gasWorkers = new ArrayList<>();
      mineralWorkers = new ArrayList<>();
      scoutingWorkers = new ArrayList<>();
      bases = new ArrayList<>();
      System.out.println("Finished creating worker manager");
      //      aGame.self().getUnits().forEach(this::addNewWorker);
   }

   public void addNewWorker(Unit aUnit) {
      System.out.println("Adding new worker");
      if(aUnit.isIdle() && aUnit.getType().isWorker()) {
         if(shouldCollectGas()) {
            addNewGaser(aUnit);
         }else if(shouldCollectMinerals()) {
            addNewMiner(aUnit);
         }else {
            addNewScouter(aUnit);
         }
         workers.add(aUnit);
      }
      if(aUnit.getType() == UnitType.Terran_Command_Center) {
         bases.add(aUnit);
      }
   }

   private boolean shouldCollectMinerals() {
      return workerTicker++ % 5 != 0;
   }

   private boolean shouldCollectGas() {
      return false;
   }

   private void addNewMiner(Unit aNewWorker) {
      Unit closestMineral = null;
      for(Unit neutralUnit : theGame.neutral().getUnits()) {
         if(neutralUnit.getType().isMineralField()) {
            if(closestMineral == null || aNewWorker.getDistance(neutralUnit) < aNewWorker.getDistance(closestMineral)) {
               closestMineral = neutralUnit;
            }
         }
      }
      if(closestMineral != null) {
         System.out.println("Gather minerals");
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
//      while(remove != null && remove.getPosition().equals(bases.get(0).getPosition())
//            && !remove.isStartLocation()) {
//         System.out.println("Getting new base to scout");
//         remove = baseLocations.remove();
//      }
      return remove.getPosition();
   }
}
