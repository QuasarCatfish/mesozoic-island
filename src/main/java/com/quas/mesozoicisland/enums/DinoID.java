package com.quas.mesozoicisland.enums;

import com.quas.mesozoicisland.util.Pair;

public enum DinoID {

	WildChicken8(-18),
	WildChicken7(-17),
	WildChicken6(-16),
	WildChicken5(-15),
	WildChicken4(-14),
	WildChicken3(-13),
	WildChicken2(-12),
	WildChicken1(-11),
	Turkey(-10),

	Chicken(0),
	Pterodactylus(1),
	Ichthyosaurus(2),
	Plesiosaurus(3),
	Mosasaurus(4),
	Megalosaurus(5),
	Iguanodon(6),
	Streptospondylus(7),
	Hylaeosaurus(8),
	Gnathosaurus(9),
	Thecodontosaurus(10),
	Plateosaurus(11),
	Poekilopleuron(12),
	Ischyrodon(13),
	Cardiodon(14),
	Suchosaurus(15),
	Pliosaurus(16),
	Cetiosaurus(17),
	Polyptychodon(18),
	Liodon(19),
	Rhamphorhynchus(20),
	Regnosaurus(21),
	Pelorosaurus(22),
	Cimoliasaurus(23),
	Tholodus(24),
	Ctenochasma(25),
	Oplosaurus(26),
	Aepisaurus(27),
	Nuthetes(28),
	Massospondylus(29),
	Palaeoscincus(30),
	Trachodon(31),
	Thespesius(32),
	Deinodon(33),
	Troodon(34),
	Stenopelix(35),
	Dimorphodon(36),
	Scelidosaurus(37),
	Hadrosaurus(38),
	Compsognathus(39),
	Astrodon(40),
	Dorygnathus(41),
	Echinodon(42),
	Scaphognathus(43),
	Archaeonectrus(44),
	Polacanthus(45),
	Euskelosaurus(46),
	Acanthopholis(47),
	Cymbospondylus(48),
	Aublysodon(49),
	Elasmosaurus(50),
	Clidastes(51),
	Ornithocheirus(52),
	Hypsilophodon(53),
	Halisaurus(54),
	Cryptosaurus(55),
	Hypsibema(56),
	Platecarpus(57),
	Hypselosaurus(58),
	Rhabdodon(59),
	Polycotylus(60),
	Struthiosaurus(61),
	Cycnorhamphus(62),
	Antrodemus(63),
	Ornithopsis(64),
	Ornithostoma(65),
	Diopecephalus(66),
	Rhomaleosaurus(67),
	Tylosaurus(68),
	Agathaumas(69),
	Eucamerotus(70),
	Liopleurodon(71),
	Colymbosaurus(72),
	Mauisaurus(73),
	Morinosaurus(74),
	Craterosaurus(75),
	Ophthalmosaurus(76),
	Coloborhynchus(77),
	Muraenosaurus(78),
	Taniwhasaurus(79),
	Bothriospondylus(80),
	Priodontognathus(81),
	Paronychodon(82),
	Macrurosaurus(83),
	Monoclonius(84),
	Dysganus(85),
	Pteranodon(86),
	Nyctosaurus(87),
	Allosaurus(88),
	Dystrophaeus(89),
	Apatosaurus(90),
	Stegosaurus(91),
	Camarasaurus(92),
	Dryptosaurus(93),
	Atlantosaurus(94),
	Amphicoelias(95),
	Titanosaurus(96),
	Nanosaurus(97),
	Diplodocus(98),
	Laosaurus(99),
	Epanterias(100);

	public static DinoID[] WILD_CHICKENS = new DinoID[] {WildChicken1, WildChicken2, WildChicken3, WildChicken4, WildChicken5, WildChicken6, WildChicken7, WildChicken8};

	private int dex;
	private DinoID(int dex) {
		this.dex = dex;
	}

	public int getDex() {
		return dex;
	}

	public Pair<Integer, Integer> getId() {
		return getId(DinosaurForm.Standard);
	}

	public Pair<Integer, Integer> getId(DinosaurForm form) {
		return new Pair<Integer, Integer>(this.dex, form.getId());
	}

	public static DinoID of(int dex) {
		for (DinoID id : values()) {
			if (id.dex == dex) {
				return id;
			}
		}
		return null;
	}
}