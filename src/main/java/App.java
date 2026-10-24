import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.InputStream;
import java.net.URL;

public class App {

    private static final String XML_VALIDE   = "cinema.xml";
    private static final String XML_INVALIDE = "cinema_invalide.xml";
    private static final String XSD          = "cinema.xsd";

    public static void main(String[] args) throws Exception {

        System.out.println("=".repeat(60));
        System.out.println("  TP1 SOA — Gestion de Cinéma (XML / XSD / XPath)");
        System.out.println("=".repeat(60));

        // ── 1. Validation du fichier XML VALIDE ──
        System.out.println("\n  1. Validation du fichier XML valide                     ");
        validateXML(XML_VALIDE, XSD);

        // ── 2. Validation du fichier XML INVALIDE ──
        System.out.println("\n  2. Validation du fichier XML invalide (test d'erreurs)  ");

        validateXML(XML_INVALIDE, XSD);

        // ── 3. Interrogation XPath ──
        System.out.println("\n  3. Interrogation XPath sur le document valide           ");

        queryXPath();
    }


    //  Validation XML / XSD

    private static void validateXML(String xmlFile, String xsdFile) {
        try {
            URL xsdUrl = App.class.getClassLoader().getResource(xsdFile);
            if (xsdUrl == null) {
                System.out.println("  ✘ Fichier XSD introuvable : " + xsdFile);
                return;
            }

            SchemaFactory factory =
                    SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = factory.newSchema(xsdUrl);
            Validator validator = schema.newValidator();

            InputStream xmlStream = App.class.getClassLoader().getResourceAsStream(xmlFile);
            if (xmlStream == null) {
                System.out.println("  ✘ Fichier XML introuvable : " + xmlFile);
                return;
            }

            validator.validate(new StreamSource(xmlStream));

            System.out.println("  ✔ Le fichier \"" + xmlFile + "\" est VALIDE selon le XSD.");

        } catch (SAXException e) {
            System.out.println("  ✘ Le fichier \"" + xmlFile + "\" est INVALIDE !");
            System.out.println("    Erreur : " + e.getMessage());
        } catch (Exception e) {
            System.out.println("  ✘ Erreur inattendue : " + e.getMessage());
        }
    }

    //  Interrogation XPath

    private static void queryXPath() throws Exception {
        InputStream xmlStream = App.class.getClassLoader().getResourceAsStream(XML_VALIDE);
        if (xmlStream == null) {
            throw new RuntimeException("Fichier introuvable dans les ressources : " + XML_VALIDE);
        }

        Document doc = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder()
                .parse(xmlStream);

        XPath xpath = XPathFactory.newInstance().newXPath();

        // ── Requête 1 : Tous les titres de films ──
        printSection("Requête 1 — Tous les titres de films");
        printXPathResults(xpath, doc, "//film/titre");

        // ── Requête 2 : Clients actifs ──
        printSection("Requête 2 — Noms des clients actifs");
        printXPathResults(xpath, doc, "//client[@actif='true']/nom");

        // ── Requête 3 : Films en promotion ──
        printSection("Requête 3 — Films en promotion");
        printXPathResults(xpath, doc, "//film[@promotion='true']/titre");

        // ── Requête 4 : Films de Christopher Nolan ──
        printSection("Requête 4 — Films réalisés par Christopher Nolan");
        printXPathResults(xpath, doc, "//film[realisateur='Christopher Nolan']/titre");

        // ── Requête 5 : Films de plus de 150 minutes ──
        printSection("Requête 5 — Films de plus de 150 minutes");
        printXPathResults(xpath, doc, "//film[@duree > 150]/titre");

        // ── Requête 6 : Films avec une note >= 9.0 ──
        printSection("Requête 6 — Films avec une note >= 9.0");
        printXPathResults(xpath, doc, "//film[@note >= 9.0]/titre");

        // ── Requête 7 : Nombre total de films ──
        printSection("Requête 7 — Nombre total de films");
        String count = xpath.evaluate("count(//film)", doc);
        System.out.println("  → " + count + " film(s)");

        // ── Requête 8 : Noms des salles IMAX ──
        printSection("Requête 8 — Salles de type IMAX");
        printXPathResults(xpath, doc, "//salle[@type='IMAX']/nomSalle");

        // ── Requête 9 : Salles climatisées ──
        printSection("Requête 9 — Salles climatisées");
        printXPathResults(xpath, doc, "//salle[@climatisee='true']/nomSalle");

        // ── Requête 10 : Séances actives ──
        printSection("Requête 10 — Dates des séances actives");
        printXPathResults(xpath, doc, "//seance[@statut='active']/date");

        // ── Requête 11 : Réservations confirmées ──
        printSection("Requête 11 — Montants des réservations confirmées");
        printXPathResults(xpath, doc, "//reservation[@statut='confirmee']/montantTotal");

        // ── Requête 12 : Clients abonnés Premium ──
        printSection("Requête 12 — Clients avec abonnement Premium");
        printXPathResults(xpath, doc, "//client[@typeAbonnement='Premium']/nom");

        // ── Requête 13 : Acteurs du film Inception ──
        printSection("Requête 13 — Acteurs du film Inception");
        printXPathResults(xpath, doc, "//film[titre='Inception']/acteurs/acteur");

        // ── Requête 14 : Films en langue française ──
        printSection("Requête 14 — Films en langue française (fr)");
        printXPathResults(xpath, doc, "//film[@langue='fr']/titre");

        // ── Requête 15 : Capacité totale de toutes les salles ──
        printSection("Requête 15 — Capacité totale de toutes les salles");
        String totalCapacite = xpath.evaluate("sum(//salle/capacite)", doc);
        System.out.println("  → " + totalCapacite + " places au total");

        // ── Requête 16 : Clients de la ville de Sfax ──
        printSection("Requête 16 — Clients de la ville de Sfax");
        printXPathResults(xpath, doc, "//client[adresse/@ville='Sfax']/nom");

        // ── Requête 17 : Nombre de réservations ──
        printSection("Requête 17 — Nombre total de réservations");
        String nbRes = xpath.evaluate("count(//reservation)", doc);
        System.out.println("  → " + nbRes + " réservation(s)");

        // ── Requête 18 : Films en Science-fiction ──
        printSection("Requête 18 — Films du genre Science-fiction");
        printXPathResults(xpath, doc, "//film[genre='Science-fiction']/titre");

        // ── Requête 19 : Séances dans la salle S1 ──
        printSection("Requête 19 — Séances dans la salle S1 (Carthage)");
        printXPathResults(xpath, doc, "//seance[@salleRef='S1']/date");

        // ── Requête 20 : Acteurs français ──
        printSection("Requête 20 — Acteurs de nationalité française (FR)");
        printXPathResults(xpath, doc, "//acteur[@nationalite='FR']");
    }

    // ── Méthode utilitaire : afficher un titre de section ──
    private static void printSection(String titre) {
        System.out.println("\n  ▸ " + titre);
        System.out.println("  " + "─".repeat(titre.length() + 2));
    }

    // ── Méthode utilitaire : évaluer et afficher les résultats XPath ──
    private static void printXPathResults(XPath xpath, Document doc, String expression)
            throws Exception {
        System.out.println("    XPath : " + expression);
        NodeList nodes = (NodeList) xpath.evaluate(expression, doc, XPathConstants.NODESET);
        if (nodes.getLength() == 0) {
            System.out.println("    (aucun résultat)");
        } else {
            for (int i = 0; i < nodes.getLength(); i++) {
                System.out.println("    - " + nodes.item(i).getTextContent());
            }
        }
    }
}