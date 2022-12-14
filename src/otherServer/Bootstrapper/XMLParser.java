package otherServer.Bootstrapper;
import javax.lang.model.util.Elements;
import javax.xml.parsers.*;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import Common.InfoNodo;
import org.w3c.dom.*;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.net.InetAddress;
import java.rmi.MarshalledObject;
import java.util.*;
import java.io.*;
import java.util.stream.Collectors;

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

        xml.append("<server name=\"" + nodes.get("s1").getidNodo() +  "\" ip=\"" + nodes.get("s1").getIp() +  "\" port=\"" + nodes.get("s1").portNet + "\"" +">");

        xml.append(generateXMLaux(nodes.get("s1"),1, bestPaths));

        xml.append("</server>");


        //this.setXMLString(xml.toString());

        return  xml.toString();

    }


    public List<Connection> getPath(InfoNodo node ,Map<InfoNodo,List<Connection>> bestPaths){
        List<Connection> con = null;

        for (InfoNodo n : bestPaths.keySet().stream().collect(Collectors.toList())){
            if(n.getIp().equals(node.getIp()) && n.portNet == node.portNet){
                con = bestPaths.get(n);
            }
        }

        return con;


    }


    public StringBuilder generateXMLaux(InfoNodo node ,int identation, Map<InfoNodo,List<Connection>> bestPaths) {
        StringBuilder aux = new StringBuilder();

        List<Connection> connections= getPath(node,bestPaths);

        if(connections != null){
            for (Connection connection :  connections){

                aux.append("<node name=\""+connection.to.getidNodo() + "\" ip=\"" + connection.to.getIp() + "\" port=\"" + connection.to.portNet + "\"" +">");
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


    public String destiny(String xmlNode) throws IOException, SAXException, ParserConfigurationException {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new InputSource(new StringReader(xmlNode)));

        Element rootElement = doc.getDocumentElement();


        return rootElement.getAttribute("ip");

    }

    public InfoNodo destinyInfoNodo(String xmlNode) throws ParserConfigurationException, IOException, SAXException {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new InputSource(new StringReader(xmlNode)));

        Element rootElement = doc.getDocumentElement();

        String ip[] = rootElement.getAttribute("ip").split("/");

        String ipToSend = ip[1];

        System.out.println("---------AQUIIIIII-------");
        System.out.println(ipToSend);


        return  new InfoNodo(InetAddress.getByName(ipToSend),Integer.parseInt(rootElement.getAttribute("port")));

    }


    /*
        Partition xml
     */

    public String innerXml(Node node) {
        DOMImplementationLS lsImpl = (DOMImplementationLS)node.getOwnerDocument().getImplementation().getFeature("LS", "3.0");
        LSSerializer lsSerializer = lsImpl.createLSSerializer();
        lsSerializer.getDomConfig().setParameter("xml-declaration", false);
        NodeList childNodes = node.getChildNodes();
        StringBuilder sb = new StringBuilder();
        sb.append(lsSerializer.writeToString(node));
        /*for (int i = 0; i < childNodes.getLength(); i++) {
            sb.append(lsSerializer.writeToString(childNodes.item(i)));
        }*/
        return sb.toString();
    }


    public Map<InfoNodo, String> partitionXML (String xmlString) throws ParserConfigurationException, IOException, SAXException {

        //parseXML(xmlString);

        Map<InfoNodo, String> partition = new HashMap<>();

        // Parse the XML string into a document object
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new InputSource(new StringReader(xmlString)));

        String version = doc.getXmlVersion();

        // Get the root element of the document
        Element rootElement = doc.getDocumentElement();
        //NodeList childNodes = doc.getChildNodes();
        NodeList childNodes = rootElement.getChildNodes();


        for (int i = 0; i<childNodes.getLength(); i++){
            Node node = childNodes.item(i);

            NodeList innerChilds = node.getChildNodes();


            // If the node is an element, print its name and value

            if (node.getNodeType() == Node.ELEMENT_NODE) {

                Element childElem = (Element) node;
                System.out.println("IP: " + childElem.getAttribute("ip") + " - Name: " + childElem.getAttribute("name") + " - Port: " + childElem.getAttribute("port"));
                String[] ipString = childElem.getAttribute("ip").split("/");
                InetAddress ipChildren = InetAddress.getByName(ipString[1]);
                String nodeValue = innerXml(node);
                partition.put(new InfoNodo(ipChildren, Integer.parseInt(childElem.getAttribute("port"))), nodeValue);

            }

        }

        return partition;


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
