-- Enigma Database Data
--
-- Model: enigma    Version: 1.0
-- Author: Zanone Jérémie
-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

-- -----------------------------------------------------
-- Schema enigma
-- -----------------------------------------------------
USE `enigma` ;

-- -----------------------------------------------------
-- Table `enigma`.`Riddle`
-- Dumping data
-- -----------------------------------------------------

SET AUTOCOMMIT=0;
INSERT INTO `enigma`.`Riddle` (`Question`,`Answer1`,`Answer2`,`Answer3`) VALUES 
('De quelle couleur est le cheval blanc de Napoléon','Blanc', null, null),
('J\'ai quelque chose dans ma poche mais ma poche est vide. Qu\'est-ce que c\'est ?','Un trou', null, null),
('J\'ai un manteau, une seule manche, et plusieurs cols. Qui suis-je ?','La terre', null, null),
('Deux canards se trouvent devant un canard, deux canards se trouvent derrière un canard et un canard est au milieu. Combien y a-t-il de canards en tout?','Il y a trois cannards', 'trois', '3'),
('si vous m\'approchez, je vous tue, mais sans moi vous ne pourriez vivre.','Le soleil', null, null),
('Je suis toujours mouillée, mais je ne suis jamais dans l\'eau. J\'habite dans un palais qui n\'est pas le mien. Qui suis je ?','La langue', null, null),
('J\'ai une gorge mais je ne peux pas parler, je coule mais ne me noie pas, j\'ai un lit mais je ne dors jamais. Qui suis-je ?','Une rivière', 'ruisseau', 'cours d\'eau'),
('J\'ai un père mais je ne suis pas son fils, j\'ai une mère mais je ne suis pas son fils. Qui suis-je ?','leur fille', null, null),
('Un médecin a soigné 11111 patients, mais aucun ne l\'a remercié pourquoi ?','Il est vétérinaire', 'vétérinaire', null),
('J\'ai 192 poule. Poule ne prend pas de \"s\" pourquoi ?','J\'ai un oeuf de poule', 'un oeuf de poule', null),
('Quel animal saute plus haut qu\'une maison ???','Tous', null, null),
('De couleur Or, je te donne la vie, tu me donnes la mort. Qui suis-je?','La bière', 'le blé', null),
('Dans une pièce rectangulaire, les 4 murs, le sol et le plafond sont remplis de miroirs. La pièce est vide, seul un homme y est présent, combien de reflets verra-t-il de lui même ?','aucun, il n\'y a pas de fenêtre', '0','aucun'),
('1. Mieux que dieu 2. Pire que le diable 3. Les pauvres en ont 4. Les riches en ont besoin 5. Si on en mange, on meurt','rien', null, null),
('Pour couvrir ma maîtresse je me gonfle et me dresse. Quand j\'ai fini mon service, je me plisse et je pisse ! Qui suis-je ? Un indice ? Pour éviter de mettre des gouttes partout... Il faut me secouer... avant de me ranger.','Un parapluie', null, null),
('Qu\'est -ce qu\'il y a au centre des hommes et des femmes  ?','Les lettres MM', 'MM', null),
('Qu\'est-ce que les hommes et les femmes ont en plein milieu des jambes ?','Les genoux', null, null),
('Un peu de vocabulaire: B.ISER, P.NIS, JOU.R','Briser punis jouer', 'Briser, punis, jouer', null),
('Un peu de vocabulaire: SAL..E, ..CULER, .APOTE','Salade, reculer, papote', 'salive, reculer, tapote', null),
('Quel organe chez l\'homme peut multiplier sa taille par six?','La pupille', null, null),
('A quoi reconnait-on le slip de Dark Vador?','son côté obscur', null, null),
('Comment appelle-t-on des chaussures d\'enterrement','des pompes funèbres', null, null),
('Comment appelle-t-on une femme qui n\'a jamais pris la pilule?','maman', null, null),
('Quel est le dessert préféré des araignées?','mouche au chocolat', 'mousse au chocolat', null),
('Que mais un rat pour se faire beau.','du masque à rat','mascara', 'un masque'),
('Comment appelle t\'on un rat sans queue ?','Un raccourci', 'un rat courcit', null),
('Quel animal a six pattes et marche sur la tête ?','un pou', null, null),
('Quelle est la différence entre une crotte de nez et un brocoli ?','les enfants ne mangent pas les brocolis','enfants mangent', null),
('Pourquoi la NASA envoie-t-elle des femmes dans l\'espace? ','Moins lourd qu\'un lave-vaisselle', 'faire la vaiselle', 'faire le ménage'),
('Quelle est la différence entre le 51 et le 69 ?',' Le 51 sent l\'anis et le 69 sent l\'anus !', 'anis sent anus', 'anis et anus'),
('Pourquoi la drogue est-elle interdite en prison','ça brûle les cellules', null, null),
('Comment dit-on témoin de Jéhovah en chinois?','Ding dong', 'Dhing dhong', null),
('Je commence par un \"e\", je finis par un \"e\" et je contiens une lettre. Qui suis-je ?','Une enveloppe', null, null),
('A Tchernobyl, comment les enfants comptent-ils jusqu\'à 33?','Sur leurs doigts', 'doigts', null),
('Un x au carré rentre dans uen forêt, il en ressort en étant x. Qu\'est ce qu\'il a fait?','Il s\'est pris une racine', 'pris une racine', 'tombé sur une racine'),
('A quoi sert Internet Explorer (EDGE)?','Télécharger Google Chrome', 'Télécharger Mozilla firefox', 'Télécharger chrome'),
('Qu\'est ce qui est rose et qui aime l\’informatique ?','Port USB', 'Porc USB', null),
('Quel est le comble pour un avion ?','D\'avoir un anti-vol', 'avoir un anti-vol', 'antivol'),
('Quel jour les poules ont-elles l\'anus dilaté au maximum?','Quand elles passent du coq à l\'âne', 'coq âne', 'du coq à l\'ane'),
('Quel est le fruit que détestent les poissons ?','la pêche', null, null),
('Plus on tire dessus, plus cela devient court.','La cigarette', 'La journée', null),
('Qu\'est ce qui est plus grand que la Tour Eiffel, mais infiniment moins lourd','Son ombre', null, null);
COMMIT;

SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
