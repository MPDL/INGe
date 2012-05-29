/*
*
* CDDL HEADER START
*
* The contents of this file are subject to the terms of the
* Common Development and Distribution License, Version 1.0 only
* (the "License"). You may not use this file except in compliance
* with the License.
*
* You can obtain a copy of the license at license/ESCIDOC.LICENSE
* or http://www.escidoc.de/license.
* See the License for the specific language governing permissions
* and limitations under the License.
*
* When distributing Covered Code, include this CDDL HEADER in each
* file and include the License file at license/ESCIDOC.LICENSE.
* If applicable, add the following below this CDDL HEADER, with the
* fields enclosed by brackets "[]" replaced with your own identifying
* information: Portions Copyright [yyyy] [name of copyright owner]
*
* CDDL HEADER END
*/

/*
* Copyright 2006-2012 Fachinformationszentrum Karlsruhe Gesellschaft
* für wissenschaftlich-technische Information mbH and Max-Planck-
* Gesellschaft zur Förderung der Wissenschaft e.V.
* All rights reserved. Use is subject to license terms.
*/ 

package test.creators;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import de.mpg.escidoc.services.common.util.creators.Author;
import de.mpg.escidoc.services.common.util.creators.AuthorDecoder;

/**
 * Tests for the AuthorDecoder class.
 *
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class AuthorDecoderTest
{
    @Test
    public void testSimpleFormat1() throws Exception
    {
        String input = "Martin de la Iglesia, Max Planck";
        
        AuthorDecoder authorDecoder = new AuthorDecoder(input);
        List<Author> bestList = authorDecoder.getBestAuthorList();
        assertNotNull(bestList);
        assertEquals(2, bestList.size());
        assertEquals("Martin", bestList.get(0).getGivenName());
        assertEquals("Max", bestList.get(1).getGivenName());
        assertEquals("de la Iglesia", bestList.get(0).getSurname());
        assertEquals("Planck", bestList.get(1).getSurname());
    }
    
    @Test
    public void testSimpleFormat2() throws Exception
    {
        String input = "Heribert Müller und Wladimir Borodin";
        
        AuthorDecoder authorDecoder = new AuthorDecoder(input);
        List<Author> bestList = authorDecoder.getBestAuthorList();
        assertNotNull(bestList);
        assertEquals(2, bestList.size());
        assertEquals("Heribert", bestList.get(0).getGivenName());
        assertEquals("Wladimir", bestList.get(1).getGivenName());
        assertEquals("Müller", bestList.get(0).getSurname());
        assertEquals("Borodin", bestList.get(1).getSurname());
    }
    
    @Test
    public void testSimpleFormat3() throws Exception
    {
        String input = "Schultze, André";
        
        AuthorDecoder authorDecoder = new AuthorDecoder(input);

        List<Author> bestList = authorDecoder.getBestAuthorList();
        assertNotNull(bestList);
        assertEquals(1, bestList.size());
        assertEquals("André", bestList.get(0).getGivenName());
        assertEquals("Schultze", bestList.get(0).getSurname());
    }
    
    @Test
    public void testSimpleFormat4() throws Exception
    {
        String input = "André Schultze";
        
        AuthorDecoder authorDecoder = new AuthorDecoder(input);

        List<Author> bestList = authorDecoder.getBestAuthorList();
        assertNotNull(bestList);
        assertEquals(1, bestList.size());
        assertEquals("André", bestList.get(0).getGivenName());
        assertEquals("Schultze", bestList.get(0).getSurname());
    }
    
    @Test
    public void testUglyWhitespaces1() throws Exception
    {
        String input = "\n\t\tAndré      Schultze\n\n\n";
        
        AuthorDecoder authorDecoder = new AuthorDecoder(input);
        
        authorDecoder.displayAuthors();
        
        List<Author> bestList = authorDecoder.getBestAuthorList();
        assertNotNull(bestList);
        assertEquals(1, bestList.size());
        assertEquals("André", bestList.get(0).getGivenName());
        assertEquals("Schultze", bestList.get(0).getSurname());
    }
    
    @Test
    public void testUglyWhitespaces2() throws Exception
    {
        String input = "\n\n      Miller, AJ\n      Smith, JR\n";
        
        AuthorDecoder authorDecoder = new AuthorDecoder(input);
        
        authorDecoder.displayAuthors();
        
        List<Author> bestList = authorDecoder.getBestAuthorList();
        assertNotNull(bestList);
        assertEquals(2, bestList.size());
        assertEquals("AJ", bestList.get(0).getGivenName());
        assertEquals("JR", bestList.get(1).getGivenName());
        assertEquals("Miller", bestList.get(0).getSurname());
        assertEquals("Smith", bestList.get(1).getSurname());
    }
    
    @Test
    @Ignore("Eastern naming not yet implemented")
    public void testCommaSeparatedChinese() throws Exception
    {
        String input = "Guanghao Jina, Dooyoung Kimb, Byeongnoh Kimb, Jin-Hee Yoonb, Dongwoo Chab,";
        
        AuthorDecoder authorDecoder = new AuthorDecoder(input);
        
        authorDecoder.displayAuthors();
        
        List<Author> bestList = authorDecoder.getBestAuthorList();
        assertNotNull(bestList);
        assertEquals(5, bestList.size());
        assertEquals("Jina", bestList.get(0).getGivenName());
        assertEquals("Chab", bestList.get(4).getGivenName());
        assertEquals("Guanghao", bestList.get(0).getSurname());
        assertEquals("Dongwoo", bestList.get(4).getSurname());
    }

    @Test
    public void testEndnoteFormat() throws Exception
    {
        String input = "Dorin, Phillipe H.\nKaiser, Reginald\nKönig, Ludwig I.";
        
        AuthorDecoder authorDecoder = new AuthorDecoder(input);

        List<Author> bestList = authorDecoder.getBestAuthorList();
        assertNotNull(bestList);
        assertEquals(3, bestList.size());
        assertEquals("Phillipe H.", bestList.get(0).getGivenName());
        assertEquals("Ludwig I.", bestList.get(2).getGivenName());
        assertEquals("Dorin", bestList.get(0).getSurname());
        assertEquals("König", bestList.get(2).getSurname());
    }
    
    @Test
    public void testHeavyInputString1() throws Exception
    {
        String input = "Abbott, B. P.; Abbott, R.; Adhikari, R.; Ajith, P.; Allen, B.; Allen, G.; Amin, R. S.;" +
        		" Anderson, S. B.; Anderson, W. G.; Arain, M. A.; Araya, M.; Armandula, H.; Armor, P.; Aso, Y.;" +
        		" Aston, S.; Aufmuth, P.; Aulbert, C.; Babak, S.; Baker, P.; Ballmer, S.; Barker, C.; Barker, D.;" +
        		" Barr, B.; Barriga, P.; Barsotti, L.; Barton, M. A.; Bartos, I.; Bassiri, R.; Bastarrika, M.;" +
        		" Behnke, B.; Benacquista, M.; Betzwieser, J.; Beyersdorf, P. T.; Bilenko, I. A.;" +
        		" Billingsley, G.; Biswas, R.; Black, E.; Blackburn, J. K.; Blackburn, L.; Blair, D.; Bland, B.;" +
        		" Bodiya, T. P.; Bogue, L.; Bork, R.; Boschi, V.; Bose, S.; Brady, P. R.; Braginsky, V. B.;" +
        		" Brau, J. E.; Bridges, D. O.; Brinkmann, M.; Brooks, A. F.; Brown, D. A.; Brummit, A.;" +
        		" Brunet, G.; Bullington, A.; Buonanno, A.; Burmeister, Oliver; Byer, R. L.; Cadonati, L.;" +
        		" Camp, J. B.; Cannizzo, J.; Cannon, K. C.; Cao, J.; Capano, C. D.; Cardenas, L.; Caride, S.;" +
        		" Castaldi, G.; Caudill, S.; Cavaglia, M.; Cepeda, C.; Chalermsongsak, T.; Chalkley, E.;" +
        		" Charlton, P.; Chatterji, S.; Chelkowski, S.; Chen, Y.; Christensen, N.; Chung, C. T. Y.;" +
        		" Clark, D.; Clark, J.; Clayton, J. H.; Cokelaer, T.; Colacino, C. N.; Conte, R.; Cook, D.;" +
        		" Corbitt, T. R. C.; Cornish, N.; Coward, D.; Coyne, D. C.; Creighton, J. D. E.;" +
        		" Creighton, T. D.; Cruise, A. M.; Culter, R. M.; Cumming, A.; Cunningham, L.; Danilishin, S. L.;" +
        		" Danzmann, K.; Daudert, B.; Davies, G.; Daw, E. J.; Debra, D.; Degallaix, J.; Dergachev, V.;" +
        		" Desai, S.; DeSalvo, R.; Dhurandhar, S.; Diaz, M.; Dietz, A.; Donovan, F.; Dooley, K. L.;" +
        		" Doomes, E. E.; Drever, R. W. P.; Dueck, J.; Duke, I.; Dumas, J. C.; Dwyer, J. G.; Echols, C.;" +
        		" Edgar, M.; Effler, A.; Ehrens, P.; Ely, G.; Espinoza, E.; Etzel, T.; Evans, M.; Evans, T.;" +
        		" Fairhurst, S.; Faltas, Y.; Fan, Y.; Fazi, D.; Fehrmann, H.; Finn, L. S.; Flasch, K.;" +
        		" Foley, S.; Forrest, C.; Fotopoulos, N.; Franzen, A.; Frede, M.; Frei, M.; Frei, Z.;" +
        		" Freise, A.; Frey, R.; Fricke, T.; Fritschel, P.; Frolov, V. V.; Fyffe, M.; Galdi, V.;" +
        		" Garofoli, J. A.; Gholami, I.; Giaime, J. A.; Giampanis, S.; Giardina, K. D.; Goda, K.;" +
        		" Goetz, E.; Goggin, L. M.; Gonzalez, G.; Gorodetsky, M. L.; Gossler, S.; Gouaty, R.; Grant, A.;" +
        		" Gras, S.; Gray, C.; Gray, M.; Greenhalgh, R. J. S.; Gretarsson, A. M.; Grimaldi, F.;" +
        		" Grosso, R.; Grote, H.; Grunewald, S.; Guenther, M.; Gustafson, E. K.; Gustafson, R.; Hage, B.;" +
        		" Hallam, J. M.; Hammer, D.; Hammond, G. D.; Hanna, C.; Hanson, J.; Harms, J.; Harry, G. M.;" +
        		" Harry, I. W.; Harstad, E. D.; Haughian, K.; Hayama, K.; Heefner, J.; Heng, I. S.;" +
        		" Heptonstall, A.; Hewitson, M.; Hild, S.; Hirose, E.; Hoak, D.; Hodge, K. A.; Holt, K.;" +
        		" Hosken, D. J.; Hough, J.; Hoyland, D.; Hughey, B.; Huttner, S. H.; Ingram, D. R.; Isogai, T.;" +
        		" Ito, M.; Ivanov, A.; Johnson, B.; Johnson, W. W.; Jones, D. I.; Jones, G.; Jones, R.; Ju, L.;" +
        		" Kalmus, P.; Kalogera, V.; Kandhasamy, S.; Kanner, J.; Kasprzyk, D.; Katsavounidis, E.;" +
        		" Kawabe, K.; Kawamura, S.; Kawazoe, F.; Kells, W.; Keppel, D. G.; Khalaidovski, A.;" +
        		" Khalili, F. Y.; Khan, R.; Khazanov, E.; King, P.; Kissel, J. S.; Klimenko, S.; Kokeyama, K.;" +
        		" Kondrashov, V.; Kopparapu, R.; Koranda, S.; Kozak, D.; Krishnan, B.; Kumar, R.; Kwee, P.;" +
        		" Laljani, V.; Lam, P. K.; Landry, M.; Lantz, B.; Lazzarini, A.; Lei, H.; Lei, M.;" +
        		" Leindecker, N.; Leonor, I.; Li, C.; Lin, H.; Lindquist, P. E.; Littenberg, T. B.;" +
        		" Lockerbie, N. A.; Lodhia, D.; Longo, M.; Lormand, M.; Lu, P.; Lubinski, M.; Lucianetti, A.;" +
        		" Luck, H.; Lundgren, A.; Machenschalk, B.; MacInnis, M.; Mageswaran, M.; Mailand, K.;" +
        		" Mandel, I.; Mandic, V.; Marka, S.; Marka, Z.; Markosyan, A.; Markowitz, J.; Maros, E.;" +
        		" Martin, I. W.; Martin, R. M.; Marx, J. N.; Mason, K.; Matichard, F.; Matone, L.;" +
        		" Matzner, R. A.; Mavalvala, N.; McCarthy, R.; McClelland, D. E.; McGuire, S. C.; McHugh, M.;" +
        		" McIntyre, G.; McKechan, D. J. A.; McKenzie, K.; Mehmet, M.; Melatos, A.; Melissinos, A. C.;" +
        		" Menendez, D. F.; Mendell, G.; Mercer, R. A.; Meshkov, S.; Messenger, C.; Meyer, M. S.;" +
        		" Miller, J.; Minelli, J.; Mino, Y.; Mitrofanov, V. P.; Mitselmakher, G.; Mittleman, R.;" +
        		" Miyakawa, O.; Moe, B.; Mohanty, S. D.; Mohapatra, S. R. P.; Moreno, G.; Morioka, T.;" +
        		" Mors, K.; Mossavi, K.; MowLowry, C.; Mueller, G.; Muller-Ebhardt, H.; Muhammad, D.;" +
        		" Mukherjee, S.; Mukhopadhyay, H.; Mullavey, A.; Munch, J.; Murray, P. G.; Myers, E.; Myers, J.;" +
        		" Nash, T.; Nelson, J.; Newton, G.; Nishizawa, A.; Numata, K.; O'Dell, J.; O'Reilly, B.;" +
        		" O'Shaughnessy, R.; Ochsner, E.; Ogin, G. H.; Ottaway, D. J.; Ottens, R. S.; Overmier, H.;" +
        		" Owen, B. J.; Pan, Y.; Pankow, C.; Papa, M. A.; Parameshwaraiah, V.; Patel, P.; Pedraza, M.;" +
        		" Penn, S.; Perraca, A.; Pierro, V.; Pinto, I. M.; Pitkin, M.; Pletsch, H. J.; Plissi, M. V.;" +
        		" Postiglione, F.; Principe, M.; Prix, R.; Prokhorov, L.; Punken, O.; Quetschke, V.;" +
        		" Raab, F. J.; Rabeling, D. S.; Radkins, H.; Raffai, P.; Raics, Z.; Rainer, N.; Rakhmanov, M.;" +
        		" Raymond, V.; Reed, C. M.; Reed, T.; Rehbein, H.; Reid, S.; Reitze, D. H.; Riesen, R.;" +
        		" Riles, K.; Rivera, B.; Roberts, P.; Robertson, N. A.; Robinson, C.; Robinson, E. L.;" +
        		" Roddy, S.; Rover, C.; Rollins, J.; Romano, J. D.; Romie, J. H.; Rowan, S.; Rudiger, A.;" +
        		" Russell, P.; Ryan, K.; Sakata, S.; de la Jordana, L. S.; Sandberg, V.; Sannibale, V.;" +
        		" Santamaria, L.; Saraf, S.; Sarin, P.; Sathyaprakash, B. S.; Sato, S.; Satterthwaite, M.;" +
        		" Saulson, P. R.; Savage, R.; Savov, P.; Scanlan, M.; Schilling, R.; Schnabel, R.;" +
        		" Schofield, R.; Schulz, B.; Schutz, B. F.; Schwinberg, P.; Scott, J.; Scott, S. M.;" +
        		" Searle, A. C.; Sears, B.; Seifert, F.; Sellers, D.; Sengupta, A. S.; Sergeev, A.;" +
        		" Shapiro, B.; Shawhan, P.; Shoemaker, D. H.; Sibley, A.; Siemens, X.; Sigg, D.; Sinha, S.;" +
        		" Sintes, A. M.; Slagmolen, B. J. J.; Slutsky, J.; Smith, J. R.; Smith, M. R.; Smith, N. D.;" +
        		" Somiya, K.; Sorazu, B.; Stein, A.; Stein, L. C.; Steplewski, S.; Stochino, A.; Stone, R.;" +
        		" Strain, K. A.; Strigin, S.; Stroeer, A.; Stuver, A. L.; Summerscales, T. Z.; Sun, K. X.;" +
        		" Sung, M.; Sutton, P. J.; Szokoly, G. P.; Talukder, D.; Tang, L.; Tanner, D. B.;" +
        		" Tarabrin, S. P.; Taylor, J. R.; Taylor, R.; Thacker, J.; Thorne, K. A.; Thorne, K. S.;" +
        		" Thuring, A.; Tokmakov, K. V.; Torres, C.; Torrie, C.; Traylor, G.; Trias, M.; Ugolini, D.;" +
        		" Ulmen, J.; Urbanek, K.; Vahlbruch, H.; Vallisneri, M.; Van Den Broeck, C.;" +
        		" van der Sluys, M. V.; van Veggel, A. A.; Vass, S.; Vaulin, R.; Vecchio, A.; Veitch, J.;" +
        		" Veitch, P.; Veltkamp, C.; Villar, A.; Vorvick, C.; Vyachanin, S. P.; Waldman, S. J.;" +
        		" Wallace, L.; Ward, R. L.; Weidner, A.; Weinert, M.; Weinstein, A. J.; Weiss, R.; Wen, L.;" +
        		" Wen, S.; Wette, K.; Whelan, J. T.; Whitcomb, S. E.; Whiting, B. F.; Wilkinson, C.;" +
        		" Willems, P. A.; Williams, H. R.; Williams, L.; Willke, B.; Wilmut, I.; Winkelmann, L.;" +
        		" Winkler, W.; Wipf, C. C.; Wiseman, A. G.; Woan, G.; Wooley, R.; Worden, J.; Wu, W.;" +
        		" Yakushin, I.; Yamamoto, H.; Yan, Z.; Yoshida, S.; Zanolin, M.; Zhang, J.; Zhang, L.; Zhao, C.;" +
        		" Zotov, N.; Zucker, M. E.; zur Muhlen, H.; Zweizig, J.";
        
        long start = new Date().getTime();
        AuthorDecoder authorDecoder = new AuthorDecoder(input);
        long end = new Date().getTime();
        
        assertTrue("Method took too long, " + (end - start) + "ms.", (end - start) < 20000);
        
        List<Author> bestList = authorDecoder.getBestAuthorList();

        assertNotNull(bestList);
        assertEquals(506, bestList.size());
        assertEquals("B. P.", bestList.get(0).getGivenName());
        assertEquals("J.", bestList.get(505).getGivenName());
        assertEquals("Abbott", bestList.get(0).getSurname());
        assertEquals("Zweizig", bestList.get(505).getSurname());
        
    }
    
    @Test
    public void testHeavyInputString2() throws Exception
    {
        String input = "B. P. Abbott,17 R. Abbott,17 R. Adhikari,17 P. Ajith,2 B. Allen,2, 60 G. Allen,35 R. S. Amin,21 S. B. Anderson,17"
                + "W. G. Anderson,60 M. A. Arain,47 M. Araya,17 H. Armandula,17 P. Armor,60 Y. Aso,17 S. Aston,46 P. Aufmuth,16 C. Aulbert,2"
                + "S. Babak,1 P. Baker,24 S. Ballmer,17 C. Barker,18 D. Barker,18 B. Barr,48 P. Barriga,59 L. Barsotti,20 M. A. Barton,17"
                + "I. Bartos,10 R. Bassiri,48 M. Bastarrika,48 B. Behnke,1 M. Benacquista,42 J. Betzwieser,17 P. T. Beyersdorf,31 I. A. Bilenko,25"
                + "G. Billingsley,17 R. Biswas,60 E. Black,17 J. K. Blackburn,17 L. Blackburn,20 D. Blair,59 B. Bland,18 T. P. Bodiya,20 L. Bogue,19"
                + "R. Bork,17 V. Boschi,17 S. Bose,61 P. R. Brady,60 V. B. Braginsky,25 J. E. Brau,53 D. O. Bridges,19 M. Brinkmann,2"
                + "A. F. Brooks,17 D. A. Brown,36 A. Brummit,30 G. Brunet,20 A. Bullington,35 A. Buonanno,49 O. Burmeister,2 R. L. Byer,35"
                + "L. Cadonati,50 J. B. Camp,26 J. Cannizzo,26 K. C. Cannon,17 J. Cao,20 C. D. Capano,36 L. Cardenas,17 S. Caride,51"
                + "G. Castaldi,56 S. Caudill,21 M. Cavagli`a,39 C. Cepeda,17 T. Chalermsongsak,17 E. Chalkley,48 P. Charlton,9 S. Chatterji,17"
                + "S. Chelkowski,46 Y. Chen,1, 6 N. Christensen,8 C. T. Y. Chung,38 D. Clark,35 J. Clark,7 J. H. Clayton,60 T. Cokelaer,7"
                + "C. N. Colacino,12 R. Conte,55 D. Cook,18 T. R. C. Corbitt,20 N. Cornish,24 D. Coward,59 D. C. Coyne,17 J. D. E. Creighton,60"
                + "T. D. Creighton,42 A. M. Cruise,46 R. M. Culter,46 A. Cumming,48 L. Cunningham,48 S. L. Danilishin,25 K. Danzmann,2, 16"
                + "B. Daudert,17 G. Davies,7 E. J. Daw,40 D. DeBra,35 J. Degallaix,2 V. Dergachev,51 S. Desai,37 R. DeSalvo,17"
                + "S. Dhurandhar,15 M. D´ıaz,42 A. Dietz,7 F. Donovan,20 K. L. Dooley,47 E. E. Doomes,34 R. W. P. Drever,5 J. Dueck,2"
                + "I. Duke,20 J. -C. Dumas,59 J. G. Dwyer,10 C. Echols,17 M. Edgar,48 A. Effler,18 P. Ehrens,17 G. Ely,8 E. Espinoza,17"
                + "T. Etzel,17 M. Evans,20 T. Evans,19 S. Fairhurst,7 Y. Faltas,47 Y. Fan,59 D. Fazi,17 H. Fehrmann,2 L. S. Finn,37 K. Flasch,60"
                + "S. Foley,20 C. Forrest,54 N. Fotopoulos,60 A. Franzen,16 M. Frede,2 M. Frei,41 Z. Frei,12 A. Freise,46 R. Frey,53 T. Fricke,19"
                + "P. Fritschel,20 V. V. Frolov,19 M. Fyffe,19 V. Galdi,56 J. A. Garofoli,36 I. Gholami,1 J. A. Giaime,21, 19 S. Giampanis,2"
                + "K. D. Giardina,19 K. Goda,20 E. Goetz,51 L. M. Goggin,60 G. Gonz´alez,21 M. L. Gorodetsky,25 S. Goßler,2 R. Gouaty,21"
                + "A. Grant,48 S. Gras,59 C. Gray,18 M. Gray,4 R. J. S. Greenhalgh,30 A. M. Gretarsson,11 F. Grimaldi,20 R. Grosso,42"
                + "H. Grote,2 S. Grunewald,1 M. Guenther,18 E. K. Gustafson,17 R. Gustafson,51 B. Hage,16 J. M. Hallam,46 D. Hammer,60"
                + "G. D. Hammond,48 C. Hanna,17 J. Hanson,19 J. Harms,52 G. M. Harry,20 I. W. Harry,7 E. D. Harstad,53 K. Haughian,48"
                + "K. Hayama,42 J. Heefner,17 I. S. Heng,48 A. Heptonstall,17 M. Hewitson,2 S. Hild,46 E. Hirose,36 D. Hoak,19 K. A. Hodge,17"
                + "K. Holt,19 D. J. Hosken,45 J. Hough,48 D. Hoyland,59 B. Hughey,20 S. H. Huttner,48 D. R. Ingram,18 T. Isogai,8 M. Ito,53"
                + "A. Ivanov,17 B. Johnson,18 W. W. Johnson,21 D. I. Jones,57 G. Jones,7 R. Jones,48 L. Ju,59 P. Kalmus,17 V. Kalogera,28"
                + "S. Kandhasamy,52 J. Kanner,49 D. Kasprzyk,46 E. Katsavounidis,20 K. Kawabe,18 S. Kawamura,27 F. Kawazoe,2 W. Kells,17"
                + "D. G. Keppel,17 A. Khalaidovski,2 F. Y. Khalili,25 R. Khan,10 E. Khazanov,14 P. King,17 J. S. Kissel,21 S. Klimenko,47"
                + "K. Kokeyama,27 V. Kondrashov,17 R. Kopparapu,37 S. Koranda,60 D. Kozak,17 B. Krishnan,1 R. Kumar,48 P. Kwee,16"
                + "V. Laljani,5 P. K. Lam,4 M. Landry,18 B. Lantz,35 A. Lazzarini,17 H. Lei,42 M. Lei,17 N. Leindecker,35 I. Leonor,53 C. Li,6"
                + "H. Lin,47 P. E. Lindquist,17 T. B. Littenberg,24 N. A. Lockerbie,58 D. Lodhia,46 M. Longo,56 M. Lormand,19 P. Lu,35"
                + "M. Lubinski,18 A. Lucianetti,47 H. L¨uck,2, 16 A. Lundgren,36 B. Machenschalk,1 M. MacInnis,20 M. Mageswaran,17"
                + "K. Mailand,17 I. Mandel,28 V. Mandic,52 S. M´arka,10 Z. M´arka,10 A. Markosyan,35 J. Markowitz,20 E. Maros,17 I. W. Martin,48"
                + "R. M. Martin,47 J. N. Marx,17 K. Mason,20 F. Matichard,21 L. Matone,10 R. A. Matzner,41 N. Mavalvala,20 R. McCarthy,18"
                + "D. E. McClelland,4 S. C. McGuire,34 M. McHugh,23 G. McIntyre,17 D. J. A. McKechan,7 K. McKenzie,4 M. Mehmet,2"
                + "A. Melatos,38 A. C. Melissinos,54 D. F. Men´endez,37 G. Mendell,18 R. A. Mercer,60 S. Meshkov,17 C. Messenger,2"
                + "M. S. Meyer,19 J. Miller,48 J. Minelli,37 Y. Mino,6 V. P. Mitrofanov,25 G. Mitselmakher,47 R. Mittleman,20 O. Miyakawa,17"
                + "B. Moe,60 S. D. Mohanty,42 S. R. P. Mohapatra,50 G. Moreno,18 T. Morioka,27 K. Mors,2 K. Mossavi,2 C. MowLowry,4"
                + "G. Mueller,47 H. M¨uller-Ebhardt,2 D. Muhammad,19 S. Mukherjee,42 H. Mukhopadhyay,15 A. Mullavey,4 J. Munch,45"
                + "P. G. Murray,48 E. Myers,18 J. Myers,18 T. Nash,17 J. Nelson,48 G. Newton,48 A. Nishizawa,27 K. Numata,26 J. O’Dell,30"
                + "B. O’Reilly,19 R. O’Shaughnessy,37 E. Ochsner,49 G. H. Ogin,17 D. J. Ottaway,45 R. S. Ottens,47 H. Overmier,19 B. J. Owen,37"
                + "Y. Pan,49 C. Pankow,47 M. A. Papa,1, 60 V. Parameshwaraiah,18 P. Patel,17 M. Pedraza,17 S. Penn,13 A. Perraca,46 V. Pierro,56"
                + "I. M. Pinto,56 M. Pitkin,48 H. J. Pletsch,2 M. V. Plissi,48 F. Postiglione,55 M. Principe,56 R. Prix,2 L. Prokhorov,25 O. Punken,2"
                + "V. Quetschke,47 F. J. Raab,18 D. S. Rabeling,4 H. Radkins,18 P. Raffai,12 Z. Raics,10 N. Rainer,2 M. Rakhmanov,42"
                + "V. Raymond,28 C. M. Reed,18 T. Reed,22 H. Rehbein,2 S. Reid,48 D. H. Reitze,47 R. Riesen,19 K. Riles,51 B. Rivera,18"
                + "P. Roberts,3 N. A. Robertson,17, 48 C. Robinson,7 E. L. Robinson,1 S. Roddy,19 C. R¨over,2 J. Rollins,10 J. D. Romano,42"
                + "J. H. Romie,19 S. Rowan,48 A. R¨udiger,2 P. Russell,17 K. Ryan,18 S. Sakata,27 L. Sancho de la Jordana,44 V. Sandberg,18"
                + "V. Sannibale,17 L. Santamar´ıa,1 S. Saraf,32 P. Sarin,20 B. S. Sathyaprakash,7 S. Sato,27 M. Satterthwaite,4 P. R. Saulson,36"
                + "R. Savage,18 P. Savov,6 M. Scanlan,22 R. Schilling,2 R. Schnabel,2 R. Schofield,53 B. Schulz,2 B. F. Schutz,1, 7 P. Schwinberg,18"
                + "J. Scott,48 S. M. Scott,4 A. C. Searle,17 B. Sears,17 F. Seifert,2 D. Sellers,19 A. S. Sengupta,17 A. Sergeev,14 B. Shapiro,20"
                + "P. Shawhan,49 D. H. Shoemaker,20 A. Sibley,19 X. Siemens,60 D. Sigg,18 S. Sinha,35 A. M. Sintes,44 B. J. J. Slagmolen,4"
                + "J. Slutsky,21 J. R. Smith,36 M. R. Smith,17 N. D. Smith,20 K. Somiya,6 B. Sorazu,48 A. Stein,20 L. C. Stein,20 S. Steplewski,61"
                + "A. Stochino,17 R. Stone,42 K. A. Strain,48 S. Strigin,25 A. Stroeer,26 A. L. Stuver,19 T. Z. Summerscales,3 K. -X. Sun,35"
                + "M. Sung,21 P. J. Sutton,7 G. P. Szokoly,12 D. Talukder,61 L. Tang,42 D. B. Tanner,47 S. P. Tarabrin,25 J. R. Taylor,2"
                + "R. Taylor,17 J. Thacker,19 K. A. Thorne,19 K. S. Thorne,6 A. Th¨uring,16 K. V. Tokmakov,48 C. Torres,19 C. Torrie,17"
                + "G. Traylor,19 M. Trias,44 D. Ugolini,43 J. Ulmen,35 K. Urbanek,35 H. Vahlbruch,16 M. Vallisneri,6 C. Van Den Broeck,7"
                + "M. V. van der Sluys,28 A. A. van Veggel,48 S. Vass,17 R. Vaulin,60 A. Vecchio,46 J. Veitch,46 P. Veitch,45 C. Veltkamp,2"
                + "A. Villar,17 C. Vorvick,18 S. P. Vyachanin,25 S. J. Waldman,20 L. Wallace,17 R. L. Ward,17 A. Weidner,2 M. Weinert,2"
                + "A. J. Weinstein,17 R. Weiss,20 L. Wen,6, 59 S. Wen,21 K. Wette,4 J. T. Whelan,1, 29 S. E. Whitcomb,17 B. F. Whiting,47"
                + "C. Wilkinson,18 P. A. Willems,17 H. R. Williams,37 L. Williams,47 B. Willke,2, 16 I. Wilmut,30 L. Winkelmann,2 W. Winkler,2"
                + "C. C. Wipf,20 A. G. Wiseman,60 G. Woan,48 R. Wooley,19 J. Worden,18 W. Wu,47 I. Yakushin,19 H. Yamamoto,17 Z. Yan,59"
                + "S. Yoshida,33 M. Zanolin,11 J. Zhang,51 L. Zhang,17 C. Zhao,59 N. Zotov,22 M. E. Zucker,20 H. zur M¨uhlen,16 and J. Zweizig17";
        
        long start = new Date().getTime();
        AuthorDecoder authorDecoder = new AuthorDecoder(input);
        long end = new Date().getTime();
        
        assertTrue("Method took too long, " + (end - start) + "ms.", (end - start) < 20000);
        
        List<Author> bestList = authorDecoder.getBestAuthorList();

        assertNotNull(bestList);
        assertEquals(506, bestList.size());
        assertEquals("B. P.", bestList.get(0).getGivenName());
        assertEquals("J.", bestList.get(505).getGivenName());
        assertEquals("Abbott", bestList.get(0).getSurname());
        assertEquals("Zweizig", bestList.get(505).getSurname());
        
    }    
    @Test
    public void testHeavyInputString3() throws Exception
    {
        String input = "B Abbott13, R Abbott13, R Adhikari14, B Allen39, R Amin34," +
                "S B Anderson13,W G Anderson29, M Araya13, H Armandula13, F Asiri13," +
                "P Aufmuth31, C Aulbert1, S Babak7, R Balasubramanian7, S Ballmer14," +
                "B C Barish13, D Barker15, C Barker-Patton15, M Barnes13, B Barr35," +
                "M A Barton13, K Bayer14, R Beausoleil26, K Belczynski23, R Bennett35," +
                "S J Berukoff1, J Betzwieser14, B Bhawal13, G Billingsley13, E Black13," +
                "K Blackburn13, B Bland-Weaver15, B Bochner14, L Bogue13, R Bork13," +
                "S Bose40, P R Brady39, J E Brau37, D A Brown39, S Brozek31," +
                "A Bullington26, A Buonanno6, R Burgess14, D Busby13, W E Butler38," +
                "R L Byer26, L Cadonati14, G Cagnoli35, J B Camp21, C A Cantley35," +
                "L Cardenas13, K Carter16, M M Casey35, J Castiglione34, A Chandler13," +
                "J Chapsky13, P Charlton13, S Chatterji14, Y Chen6, V Chickarmane17," +
                "D Chin36, N Christensen8, D Churches7, C Colacino31,2, R Coldwell34," +
                "M Coles16, D Cook15, T Corbitt14, D Coyne13, J D E Creighton39," +
                "T D Creighton13, D R M Crooks35, P Csatorday14, B J Cusack3, C Cutler1," +
                "E D’Ambrosio13, K Danzmann31,2,20, R Davies7, E Daw17, D DeBra26," +
                "T Delker34, R DeSalvo13, S Dhurandar12, M D´ıaz29, H Ding13," +
                "RWPDrever4, R J Dupuis35, C Ebeling8, J Edlund13, P Ehrens13," +
                "E J Elliffe35, T Etzel13, M Evans13, T Evans16, C Fallnich31, D Farnham13," +
                "M M Fejer26, M Fine13, L S Finn28, ´E Flanagan9, A Freise2, R Frey37," +
                "P Fritschel14, V Frolov16, M Fyffe16, K S Ganezer5, J A Giaime17," +
                "A Gillespie13, K Goda14, G Gonz´alez17, S Goßler31, P Grandcl´ement23," +
                "A Grant35, C Gray15, A M Gretarsson16, D Grimmett13, H Grote2," +
                "S Grunewald1, M Guenther15, E Gustafson26, R Gustafson36," +
                "W O Hamilton17, M Hammond16, J Hanson16, C Hardham26, G Harry14," +
                "A Hartunian13, J Heefner13, Y Hefetz14, G Heinzel2, I S Heng31," +
                "M Hennessy26, N Hepler28, A Heptonstall35, M Heurs31, M Hewitson35," +
                "N Hindman15, P Hoang13, J Hough35, M Hrynevych13, W Hua26," +
                "R Ingley33, M Ito37, Y Itoh1, A Ivanov13, O Jennrich35, WW Johnson17," +
                "W Johnston29, L Jones13, D Jungwirth13, V Kalogera23," +
                "E Katsavounidis14, K Kawabe20,2, S Kawamura22,W Kells13, J Kern16," +
                "A Khan16, S Killbourn35, C J Killow35, C Kim23, C King13," +
                "P King13, S Klimenko34, P Kloevekorn2, S Koranda39, K K¨otter31," +
                "J Kovalik16, D Kozak13, B Krishnan1, M Landry15, J Langdale16," +
                "B Lantz26, R Lawrence14, A Lazzarini13, M Lei13, V Leonhardt31," +
                "I Leonor37, K Libbrecht13, P Lindquist13, S Liu13, J Logan13," +
                "M Lormand16, M Lubinski15, H L¨uck31,2, T T Lyons13, B Machenschalk1," +
                "M MacInnis14, M Mageswaran13, K Mailand13, W Majid13, M Malec31," +
                "F Mann13, A Marin14, S M´arka13, E Maros13, J Mason13, K Mason14," +
                "O Matherny15, L Matone15, N Mavalvala14, R McCarthy15," +
                "D E McClelland3, M McHugh19, P McNamara35, G Mendell15," +
                "S Meshkov13, C Messenger33, G Mitselmakher34, R Mittleman14," +
                "O Miyakawa13, S Miyoki13, S Mohanty1, G Moreno15, K Mossavi2," +
                "B Mours13, G Mueller34, S Mukherjee1, J Myers15, S Nagano2, T Nash10," +
                "H Naundorf1, R Nayak12, G Newton35, F Nocera13, P Nutzman23," +
                "T Olson24, B O’Reilly16, D J Ottaway14, A Ottewill39, D Ouimette13," +
                "H Overmier16, B J Owen28, M A Papa1, C Parameswariah16," +
                "V Parameswariah15, M Pedraza13, S Penn11, M Pitkin35, M Plissi35," +
                "M Pratt14, V Quetschke31, F Raab15, H Radkins15, R Rahkola37," +
                "M Rakhmanov34, S R Rao13, D Redding13, MWRegehr13, T Regimbau14," +
                "K T Reilly13, K Reithmaier13, D H Reitze34, S Richman14, R Riesen16," +
                "K Riles36, A Rizzi16, D I Robertson35, N A Robertson35,26, L Robison13," +
                "S Roddy16, J Rollins14, J D Romano29, J Romie13, H Rong34, D Rose13," +
                "E Rotthoff28, S Rowan35, A R¨udiger20,2, P Russell13, K Ryan15," +
                "I Salzman13, G H Sanders13, V Sannibale13, B Sathyaprakash7," +
                "P R Saulson27, R Savage15, A Sazonov34, R Schilling20,2, K Schlaufman28," +
                "V Schmidt13, R Schofield37, M Schrempel31, B F Schutz1,7," +
                "P Schwinberg15, S M Scott3, A C Searle3, B Sears13, S Seel13," +
                "A S Sengupta12, C A Shapiro28, P Shawhan13, D H Shoemaker14," +
                "Q Z Shu34, A Sibley16, X Siemens39, L Sievers13, D Sigg15, A M Sintes1,32," +
                "K Skeldon35, J R Smith2, M Smith14, M R Smith13, P Sneddon35," +
                "R Spero13, G Stapfer16, K A Strain35, D Strom37, A Stuver28," +
                "T Summerscales28, M C Sumner13, P J Sutton28, J Sylvestre13," +
                "A Takamori13, D B Tanner34, H Tariq13, I Taylor7, R Taylor13," +
                "K S Thorne6, M Tibbits28, S Tilav13, M Tinto4, C Torres29, C Torrie13,35," +
                "S Traeger31, G Traylor16, W Tyler13, D Ugolini30, M Vallisneri6," +
                "M van Putten14, S Vass13, A Vecchio33, C Vorvick15, LWallace13," +
                "H Walther20, H Ward35, B Ware13, K Watts16, D Webber13," +
                "A Weidner20,2, U Weiland31, A Weinstein13, R Weiss14, H Welling31," +
                "LWen13, S Wen17, J TWhelan19, S EWhitcomb13, B FWhiting34," +
                "P A Willems13, P R Williams1, R Williams4, BWillke31,2, A Wilson13," +
                "B JWinjum28,W Winkler20,2, S Wise34, A G Wiseman39, G Woan35," +
                "R Wooley16, JWorden15, I Yakushin16, H Yamamoto13, S Yoshida25," +
                "I Zawischa31, L Zhang13, N Zotov18, M Zucker16, J Zweizig13";
        
        long start = new Date().getTime();
        AuthorDecoder authorDecoder = new AuthorDecoder(input);
        long end = new Date().getTime();
        
        assertTrue("Method took too long, " + (end - start) + "ms.", (end - start) < 20000);
        
        List<Author> bestList = authorDecoder.getBestAuthorList();

        assertNotNull(bestList);
        assertEquals(368, bestList.size());
        assertEquals("B", bestList.get(0).getGivenName());
        assertEquals("J", bestList.get(367).getGivenName());
        assertEquals("Abbott", bestList.get(0).getSurname());
        assertEquals("Zweizig", bestList.get(367).getSurname());
        
    }
}
