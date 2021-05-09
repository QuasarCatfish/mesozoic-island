package com.quas.mesozoicisland.testing;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import com.quas.mesozoicisland.enums.DinosaurForm;
import com.quas.mesozoicisland.objects.Dinosaur;

public class WikiDinosaurs {

	public static void run() {
		
		try (PrintWriter out = new PrintWriter(new File("dinosaurs.txt"))) {
			out.println("{| class = \"wikitable sortable\"");
			out.println("! ID !! Dinosaur Name !! Element !! Rarity !! Health !! Attack !! Defense !! Classification");
			
			for (Dinosaur d : Dinosaur.values()) {
				if (d.getDinosaurForm() != DinosaurForm.Prismatic) continue;
				
				System.out.println("Processing " + d.getDinosaurName());
				
				out.println("|-");
				if (d.getDinosaurForm() == DinosaurForm.Standard) {
					out.printf("| %s || [[%s]] || {{%s}} || {{%s}} || %,d || %,d || %,d || %s\n", d.getId(), d.getDinosaurName(), d.getElement(), d.getRarity(), d.getHealth(), d.getAttack(), d.getDefense(), d.getCreatureType());
				} else {
					Dinosaur base = Dinosaur.getDinosaur(d.getDex(), DinosaurForm.Standard.getId());
					out.printf("| %s || [[%s|%s]] || {{%s}} || {{%s}} || %,d || %,d || %,d || %s\n", d.getId(), base.getDinosaurName(), d.getDinosaurName(), d.getElement(), d.getRarity(), d.getHealth(), d.getAttack(), d.getDefense(), d.getCreatureType());
				}
			}
			
			out.println("|}");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
