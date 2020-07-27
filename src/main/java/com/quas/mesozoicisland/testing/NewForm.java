package com.quas.mesozoicisland.testing;

import com.quas.mesozoicisland.JDBC;
import com.quas.mesozoicisland.enums.DinosaurForm;
import com.quas.mesozoicisland.objects.Dinosaur;

public class NewForm {

	public static void run() {
		
		DinosaurForm dform = DinosaurForm.UncapturableDungeonBoss;
		int form = dform.getId();
		String formname = dform.getName();
		int hp = 2000;
		int atk = 500;
		int def = 500;
		int rarity = -1;
		
		Dinosaur[] dinos = Dinosaur.values();
		
		for (Dinosaur d : dinos) {
			if (d.getDex() < 0) continue;
			if (d.getDinosaurForm() != DinosaurForm.Standard) continue;
			
			JDBC.executeUpdate("insert into dinosaurs values(%d, %d, '%s %s', %d, %d, %d, %d, %d, '%s', null, %d, '%s', '%s', '%s', '%s', '%s');",
					d.getDex(), form,
					formname, d.getDinosaurName(),
					d.getElement().getId(), rarity < 0 ? rarity : rarity + d.getRarity().getId(),
					d.getHealth() + hp, d.getAttack() + atk, d.getDefense() + def,
					d.getWikiLink(), // d.getImageLink(),
					d.getDiscoveryYear(), d.getCreatureType(), d.getAuthors(),
					d.getEpoch(), d.getLocation(), d.getDiet());
			System.out.printf("Added %s %s.\n", formname, d.getDinosaurName());
		}
	}
}
