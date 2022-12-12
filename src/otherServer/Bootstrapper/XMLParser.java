package otherServer.Bootstrapper;
import javax.xml.parsers.*;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import Common.InfoNodo;
import org.w3c.dom.*;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.net.InetAddress;
import java.util.*;
import java.io.*;

public class XMLParser {


    public String prettyPrintByTransformer(String xmlString, int indent, boolean ignoreDeclaration) {

        try {
            InputSource src = new InputSource(new StringReader(xmlString));
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(src);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            transformerFactory.setAttribute("indent-number", indent);
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, ignoreDeclaration ? "yes" : "no");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");

            Writer out = new StringWriter();
            transformer.transform(new DOMSource(document), new StreamResult(out));
            return out.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error occurs when pretty-printing xml:\n" + xmlString, e);
        }
    }


    public String generateXML(Map<String,InfoNodo> nodes, Map<InfoNodo,List<Connection>> bestPaths){
        //int identation = 1;

        StringBuilder xml = new StringBuilder();

        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");

        xml.append("<server name=\"" + nodes.get("s1").getidNodo() +  "\" ip=\"" + nodes.get("s1").getIp() + "\" " +">");

        xml.append(generateXMLaux(nodes.get("s1"),1, bestPaths));

        xml.append("</server>");


        //this.setXMLString(xml.toString());

        return  xml.toString();

    }

    public StringBuilder generateXMLaux(InfoNodo node ,int identation, Map<InfoNodo,List<Connection>> bestPaths) {
        StringBuilder aux = new StringBuilder();

        List<Connection> connections= bestPaths.get(node);

        if(connections != null){
            for (Connection connection :  connections){

                aux.append("<node name=\""+connection.to.getidNodo() + "\" ip=\"" + connection.to.getIp() + "\" port=\"" + connection.to.portNet + "\" " +">");
                aux.append(generateXMLaux(connection.to, identation+1, bestPaths));

                aux.append("</node>");
            }
        }


        return aux;

    }


    public byte[] fromStringToBytes(String XMLString){

        //using default charset
        byte[] xmlSocket = XMLString.getBytes();


        return xmlSocket;

    }


    public String fromBytesToString(byte[] bytes){

        String xml = new String(bytes);

        return xml;

    }








    public void parseXMLAux(NodeList nodeList) {
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);

            NodeList innerChilds = node.getChildNodes();


            // If the node is an element, print its name and value
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                System.out.println("IP: " + element.getAttribute("ip") + " - Name: " + element.getAttribute("name") + " - Port: " + element.getAttribute("port"));
            }

            parseXMLAux(innerChilds);
        }
    }


    public void parseXML(String xmlString) throws ParserConfigurationException, IOException, SAXException {

        // Parse the XML string into a document object
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new InputSource(new StringReader(xmlString)));

        // Get the root element of the document
        Element rootElement = doc.getDocumentElement();

        // Get a list of all elements in the document
        NodeList nodeList = rootElement.getChildNodes();

        parseXMLAux(nodeList);
    }


    /*
        Separate the parent from the childs info
     */
    public List<InfoNodo> splitInfo(String xmlString) throws ParserConfigurationException, IOException, SAXException {
        List<InfoNodo> info = new ArrayList<>();

        // Parse the XML string into a document object
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new InputSource(new StringReader(xmlString)));

        // Get the root element of the document
        Element rootElement = doc.getDocumentElement();

        // Get a list of all elements in the document
        NodeList childNodes = rootElement.getChildNodes();


        InfoNodo parent = new InfoNodo(InetAddress.getByName(rootElement.getAttribute("ip")), 8001,8002);


        info.add(parent);


        for (int i = 0; i<childNodes.getLength(); i++){
            Node child = childNodes.item(i);

            Element childElem  = (Element) child;

            info.add(new InfoNodo(InetAddress.getByName(childElem.getAttribute("ip")), 8001, 8002));
        }



        return  info;
    }






}
