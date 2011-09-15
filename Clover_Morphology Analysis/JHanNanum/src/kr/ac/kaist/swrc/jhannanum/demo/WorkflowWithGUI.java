/**
 * 
 */
package kr.ac.kaist.swrc.jhannanum.demo;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.DropMode;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.TransferHandler;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

import kr.ac.kaist.swrc.jhannanum.hannanum.Workflow;
import kr.ac.kaist.swrc.jhannanum.plugin.MajorPlugin.MorphAnalyzer.ChartMorphAnalyzer.ChartMorphAnalyzer;
import kr.ac.kaist.swrc.jhannanum.plugin.MajorPlugin.PosTagger.HmmPosTagger.HMMTagger;
import kr.ac.kaist.swrc.jhannanum.plugin.SupplementPlugin.MorphemeProcessor.KaistTagMappingLevel1.KaistTagMappingLevel1;
import kr.ac.kaist.swrc.jhannanum.plugin.SupplementPlugin.MorphemeProcessor.KaistTagMappingLevel2.KaistTagMappingLevel2;
import kr.ac.kaist.swrc.jhannanum.plugin.SupplementPlugin.MorphemeProcessor.KaistTagMappingLevel3.KaistTagMappingLevel3;
import kr.ac.kaist.swrc.jhannanum.plugin.SupplementPlugin.MorphemeProcessor.KaistTagMappingLevel4.KaistTagMappingLevel4;
import kr.ac.kaist.swrc.jhannanum.plugin.SupplementPlugin.MorphemeProcessor.UnknownMorphProcessor.UnknownProcessor;
import kr.ac.kaist.swrc.jhannanum.plugin.SupplementPlugin.PlainTextProcessor.InformalSentenceFilter.InformalSentenceFilter;
import kr.ac.kaist.swrc.jhannanum.plugin.SupplementPlugin.PlainTextProcessor.SentenceSegmentor.SentenceSegmentor;
import kr.ac.kaist.swrc.jhannanum.plugin.SupplementPlugin.PosProcessor.NounExtractor.NounExtractor;
import kr.ac.kaist.swrc.jhannanum.share.JSONReader;

import org.json.JSONException;

/**
 * @author Sangwon Park (hudoni@world.kaist.ac.kr), CILab, SWRC, Kaist
 *
 */
public class WorkflowWithGUI {
	private JFrame mainFrame = 	null;	/** 프로그램의 메인 프레임 */
	private JMenuBar menuBar = null;	/** 메뉴 바 */
	private JMenu menuFile = null;		/** File menu */
	private JMenuItem menuItemFileOpen = null;	/** 입력 문서 읽기 메뉴 */
    private JTree tree;
    private HashMap<String,String> pluginInfoMap = null;

    private JList listPluginMajor2= null;
    private JList listPluginMajor3= null;
    private JList listPluginSupplement1 = null;
    private JList listPluginSupplement2 = null;
    private JList listPluginSupplement3 = null;
    
    private DefaultListModel listModelMajor2 = null;
    private DefaultListModel listModelMajor3 = null;
    private DefaultListModel listModelSupplement1 = null;
    private DefaultListModel listModelSupplement2 = null;
    private DefaultListModel listModelSupplement3 = null;
    
    private JTextArea areaPluginInfo = null;
    
    private JTextArea inputTextArea = null;
    private JTextArea outputTextArea = null;
    
    private JSplitPane splitPaneTop = null;
    private JSplitPane splitPaneBottom = null;
    
    private JRadioButton radioMultiThread = null;
    private JRadioButton radioSingleThread = null;
    
    private JButton buttonActivate = null;
    private JButton buttonAnalysis = null;
    private JButton buttonReset = null;
    
    private PluginInfo selectedPlugin = null;
    private PluginInfo tempPlugin = null;
    
    private boolean multiThreadMode = true;
    private boolean activated = false;
    
    private Workflow workflow = null;

	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		WorkflowWithGUI demo = new WorkflowWithGUI();
		demo.run();
	}
	
	public void run() {
		///////////////////////////////////////////////////////////////////
		// mainFrame 기본 설정												
		///////////////////////////////////////////////////////////////////
		mainFrame = new JFrame();
		
		Toolkit kit = mainFrame.getToolkit();
		Dimension windowSize = kit.getScreenSize();
		
		mainFrame.setBounds(windowSize.width / 20, windowSize.height / 20,
				windowSize.width * 18 / 20, windowSize.height * 18 / 20);
		mainFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		mainFrame.setTitle("HanNanum Korean Morphological Analyzer - A Plug-in Component based System (GUI Demo)");
		
		Font font = new Font("MonoSpaced", Font.PLAIN, 12);
		UIManager.put("TextArea.font", font);
		
		///////////////////////////////////////////////////////////////////
		// mainFrame layout 설정											
		///////////////////////////////////////////////////////////////////
		mainFrame.setLayout(new BorderLayout());
		mainFrame.getContentPane().add(createPaneCenter(), BorderLayout.CENTER);
		mainFrame.getContentPane().add(createPaneNorth(), BorderLayout.NORTH);
		
		///////////////////////////////////////////////////////////////////
		// menu 설정											
		///////////////////////////////////////////////////////////////////		
		menuBar = new JMenuBar();
		menuFile = new JMenu("File");
		menuItemFileOpen = new JMenuItem("Open", KeyEvent.VK_O);
		
		menuItemFileOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.ALT_MASK));
		
		menuBar.add(menuFile);
		menuFile.add(menuItemFileOpen);
		mainFrame.setJMenuBar(menuBar);
		
		///////////////////////////////////////////////////////////////////
		// Event Handler 설정											
		///////////////////////////////////////////////////////////////////
		menuItemFileOpen.addActionListener(new SharedActionHandler());
		buttonActivate.addActionListener(new SharedActionHandler());
		buttonAnalysis.addActionListener(new SharedActionHandler());
		buttonReset.addActionListener(new SharedActionHandler());
		radioMultiThread.addActionListener(new SharedActionHandler());
		radioSingleThread.addActionListener(new SharedActionHandler());
		
		listPluginMajor2.addMouseListener(new PluginListMouseListener(listPluginMajor2, listModelMajor2));
		listPluginMajor3.addMouseListener(new PluginListMouseListener(listPluginMajor3, listModelMajor3));
		listPluginSupplement1.addMouseListener(new PluginListMouseListener(listPluginSupplement1, listModelSupplement1));
		listPluginSupplement2.addMouseListener(new PluginListMouseListener(listPluginSupplement2, listModelSupplement2));
		listPluginSupplement3.addMouseListener(new PluginListMouseListener(listPluginSupplement3, listModelSupplement3));
		
		listPluginMajor2.setTransferHandler(new PluginTransferHandler(PluginInfo.PHASE2, PluginInfo.MAJOR));
		listPluginMajor3.setTransferHandler(new PluginTransferHandler(PluginInfo.PHASE3, PluginInfo.MAJOR));
		listPluginSupplement1.setTransferHandler(new PluginTransferHandler(PluginInfo.PHASE1, PluginInfo.SUPPLEMENT));
		listPluginSupplement2.setTransferHandler(new PluginTransferHandler(PluginInfo.PHASE2, PluginInfo.SUPPLEMENT));
		listPluginSupplement3.setTransferHandler(new PluginTransferHandler(PluginInfo.PHASE3, PluginInfo.SUPPLEMENT));
		
		listPluginSupplement1.setDropMode(DropMode.ON_OR_INSERT);
		listPluginSupplement2.setDropMode(DropMode.ON_OR_INSERT);
		listPluginSupplement3.setDropMode(DropMode.ON_OR_INSERT);
		listPluginMajor2.setDropMode(DropMode.ON_OR_INSERT);
		listPluginMajor3.setDropMode(DropMode.ON_OR_INSERT);
		
		listPluginMajor2.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listPluginMajor3.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listPluginSupplement1.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listPluginSupplement2.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listPluginSupplement3.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		listPluginMajor2.setDragEnabled(true);
		listPluginMajor3.setDragEnabled(true);
		listPluginSupplement1.setDragEnabled(true);
		listPluginSupplement2.setDragEnabled(true);
		listPluginSupplement3.setDragEnabled(true);
		
		tree.setDragEnabled(true);
		tempPlugin = new PluginInfo("", 0, 0);
		
		workflow = new Workflow();
			
		// Frame을 화면에 출력
		mainFrame.setVisible(true);
		
		splitPaneTop.setDividerLocation(0.3);
		splitPaneBottom.setDividerLocation(0.5);
	}
	
	private JComponent createPaneNorth() {
        splitPaneTop = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPaneTop.setLeftComponent(createPluginPool());
        splitPaneTop.setRightComponent(createWorkflow());
        splitPaneTop.setOneTouchExpandable(true);

		return splitPaneTop;
	}
	
	private JComponent createPluginPool() {
        DefaultMutableTreeNode top = new DefaultMutableTreeNode("HanNanum Plug-in Pool");
        createPluginNodes(top);
        loadPluginInformation();

        tree = new JTree(top);
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        
        tree.addTreeSelectionListener(new PluginTreeSelectionListener());

        tree.putClientProperty("JTree.lineStyle", "Horizontal");

        JScrollPane treeView = new JScrollPane(tree);

        return treeView;
	}
	
    private void createPluginNodes(DefaultMutableTreeNode top) {
        DefaultMutableTreeNode phase = null;
        DefaultMutableTreeNode type = null;

        phase = new DefaultMutableTreeNode("Phase1 Plug-in. Plain Text Processing");
        type = new DefaultMutableTreeNode("Supplement Plugin");
        type.add(new DefaultMutableTreeNode(new PluginInfo("InformalSentenceFilter", PluginInfo.PHASE1, PluginInfo.SUPPLEMENT)));
        type.add(new DefaultMutableTreeNode(new PluginInfo("SentenceSegmentor", PluginInfo.PHASE1, PluginInfo.SUPPLEMENT)));
        phase.add(type);
        top.add(phase);
        
        phase = new DefaultMutableTreeNode("Phase2 Plug-in. Morphological Analysis");
        type = new DefaultMutableTreeNode("Major Plug-in");
        type.add(new DefaultMutableTreeNode(new PluginInfo("ChartMorphAnalyzer", PluginInfo.PHASE2, PluginInfo.MAJOR)));
        phase.add(type);
        type = new DefaultMutableTreeNode("Supplement Plug-in");
        type.add(new DefaultMutableTreeNode(new PluginInfo("UnknownMorphProcessor", PluginInfo.PHASE2, PluginInfo.SUPPLEMENT)));
        type.add(new DefaultMutableTreeNode(new PluginInfo("KaistTagMappingLevel1", PluginInfo.PHASE2, PluginInfo.SUPPLEMENT)));
        type.add(new DefaultMutableTreeNode(new PluginInfo("KaistTagMappingLevel2", PluginInfo.PHASE2, PluginInfo.SUPPLEMENT)));
        type.add(new DefaultMutableTreeNode(new PluginInfo("KaistTagMappingLevel3", PluginInfo.PHASE2, PluginInfo.SUPPLEMENT)));
        type.add(new DefaultMutableTreeNode(new PluginInfo("KaistTagMappingLevel4", PluginInfo.PHASE2, PluginInfo.SUPPLEMENT)));
        
        phase.add(type);
        top.add(phase);
        
        phase = new DefaultMutableTreeNode("Phase3 Plug-in. Part Of Speech Tagging");
        type = new DefaultMutableTreeNode("Major Plug-in");
        type.add(new DefaultMutableTreeNode(new PluginInfo("HmmPosTagger", PluginInfo.PHASE3, PluginInfo.MAJOR)));
        phase.add(type);
        type = new DefaultMutableTreeNode("Supplement Plug-in");
        type.add(new DefaultMutableTreeNode(new PluginInfo("NounExtractor", PluginInfo.PHASE3, PluginInfo.SUPPLEMENT)));
        phase.add(type);
        top.add(phase);
    }
    
    private void loadPluginInformation() {
    	try {
			pluginInfoMap = new HashMap<String, String>();
			pluginInfoMap.put("InformalSentenceFilter", getPluginAbstarct("conf/plugin/SupplementPlugin/PlainTextProcessor/InformalSentenceFilter.json"));
			pluginInfoMap.put("SentenceSegmentor", getPluginAbstarct("conf/plugin/SupplementPlugin/PlainTextProcessor/SentenceSegmentor.json"));
			pluginInfoMap.put("ChartMorphAnalyzer", getPluginAbstarct("conf/plugin/MajorPlugin/MorphAnalyzer/ChartMorphAnalyzer.json"));
			pluginInfoMap.put("UnknownMorphProcessor", getPluginAbstarct("conf/plugin/SupplementPlugin/MorphemeProcessor/UnknownMorphProcessor.json"));
			pluginInfoMap.put("KaistTagMappingLevel1", getPluginAbstarct("conf/plugin/SupplementPlugin/MorphemeProcessor/KaistTagMappingLevel1.json"));
			pluginInfoMap.put("KaistTagMappingLevel2", getPluginAbstarct("conf/plugin/SupplementPlugin/MorphemeProcessor/KaistTagMappingLevel2.json"));
			pluginInfoMap.put("KaistTagMappingLevel3", getPluginAbstarct("conf/plugin/SupplementPlugin/MorphemeProcessor/KaistTagMappingLevel3.json"));
			pluginInfoMap.put("KaistTagMappingLevel4", getPluginAbstarct("conf/plugin/SupplementPlugin/MorphemeProcessor/KaistTagMappingLevel4.json"));
			pluginInfoMap.put("HmmPosTagger", getPluginAbstarct("conf/plugin/MajorPlugin/PosTagger/HmmPosTagger.json"));
			pluginInfoMap.put("NounExtractor", getPluginAbstarct("conf/plugin/SupplementPlugin/PosProcessor/NounExtractor.json"));
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    private String getPluginAbstarct(String filePath) throws JSONException, IOException {
    	JSONReader json = new JSONReader(filePath);
        String res = null;
        
        res = String.format("* Name: %s\n* Type: %s\n* Version: %s\n* Author: %s\n* Description: %s\n",
        		json.getName(), json.getType(), json.getVersion(), json.getAuthor(), json.getDescription());
        return res;
    }
    
    private class PluginInfo {
    	public static final int PHASE1 = 1;
    	public static final int PHASE2 = 2;
    	public static final int PHASE3 = 3;
    	public static final int MAJOR = 1;
    	public static final int SUPPLEMENT = 2;
    	
        public String name;
        public int phase = 0;
        public int type = 0;

        public PluginInfo(String name, int phase, int type) {
        	this.name = name;
        	this.phase = phase;
        	this.type = type;
        }

        public String toString() {
            return name;
        }
    }
    
    private class PluginTreeSelectionListener implements TreeSelectionListener {
		public void valueChanged(TreeSelectionEvent e) {
	        DefaultMutableTreeNode node = (DefaultMutableTreeNode)tree.getLastSelectedPathComponent();

			if (node == null) {
				selectedPlugin = null;
				return;
			}
			
			Object nodeInfo = node.getUserObject();
			
			if (node.isLeaf()) {
				selectedPlugin = (PluginInfo)nodeInfo;
				areaPluginInfo.setText(pluginInfoMap.get((selectedPlugin.name)));
			} else {
				selectedPlugin = null;
			}
		}
    }
    
    private class PluginListMouseListener implements MouseListener {
    	private JList list = null;
    	private DefaultListModel listModel = null;
    	
    	public PluginListMouseListener(JList list, DefaultListModel listModel) {
    		this.list = list;
    		this.listModel = listModel;
    	}
    	
		@Override
		public void mouseClicked(MouseEvent e) {
			if (e.getClickCount() == 2) {
				listModel.remove(this.list.locationToIndex(e.getPoint()));
			} else {
				try {
					areaPluginInfo.setText(pluginInfoMap.get((String)listModel.get(this.list.locationToIndex(e.getPoint()))));
				} catch (Exception e1) {
				}
			}
		}

		@Override
		public void mouseEntered(MouseEvent e) {
		}

		@Override
		public void mouseExited(MouseEvent e) {
		}

		@Override
		public void mousePressed(MouseEvent e) {
		}

		@Override
		public void mouseReleased(MouseEvent e) {
		}
    }
    
	private JComponent createWorkflow() {
		JPanel workflowPanel = new JPanel(new GridLayout(1, 3));
		
	    listModelMajor2 = new DefaultListModel();
	    listModelMajor3 = new DefaultListModel();
	    listModelSupplement1 = new DefaultListModel();
	    listModelSupplement2 = new DefaultListModel();
	    listModelSupplement3 = new DefaultListModel();
	    
	    listPluginMajor2 = new JList(listModelMajor2);
	    listPluginMajor3 = new JList(listModelMajor3);
		listPluginSupplement1 = new JList(listModelSupplement1);
		listPluginSupplement2 = new JList(listModelSupplement2);
		listPluginSupplement3 = new JList(listModelSupplement3);

		// phase1
		JPanel phasePanel = new JPanel(new GridLayout(1,1));
		phasePanel.setBorder(BorderFactory.createTitledBorder("Phase1. Plain Text Processing"));
		JPanel listPanel = new JPanel(new GridLayout(1,1));
		listPanel.setBorder(BorderFactory.createTitledBorder("Supplement Plug-in"));
		JScrollPane scroll = new JScrollPane();
		scroll.setViewportView(listPluginSupplement1);
		listPanel.add(scroll);
		phasePanel.add(listPanel);
		workflowPanel.add(phasePanel);
		
		// phase2
		phasePanel = new JPanel(new GridLayout(1,2));
		phasePanel.setBorder(BorderFactory.createTitledBorder("Phase2. Morphological Analysis"));
		listPanel = new JPanel(new GridLayout(1,1));
		listPanel.setBorder(BorderFactory.createTitledBorder("Major Plug-in"));
		scroll = new JScrollPane();
		scroll.setViewportView(listPluginMajor2);
		listPanel.add(scroll);
		phasePanel.add(listPanel);
		
		listPanel = new JPanel(new GridLayout(1,1));
		listPanel.setBorder(BorderFactory.createTitledBorder("Supplement Plug-in"));
		scroll = new JScrollPane();
		scroll.setViewportView(listPluginSupplement2);
		listPanel.add(scroll);
		phasePanel.add(listPanel);
		
		workflowPanel.add(phasePanel);		
		
		// phase3
		phasePanel = new JPanel(new GridLayout(1,2));
		phasePanel.setBorder(BorderFactory.createTitledBorder("Phase3. Part Of Speech Tagging"));
		listPanel = new JPanel(new GridLayout(1,1));
		listPanel.setBorder(BorderFactory.createTitledBorder("Major Plug-in"));
		scroll = new JScrollPane();
		scroll.setViewportView(listPluginMajor3);
		listPanel.add(scroll);
		phasePanel.add(listPanel);
		
		listPanel = new JPanel(new GridLayout(1,1));
		listPanel.setBorder(BorderFactory.createTitledBorder("Supplement Plug-in"));
		scroll = new JScrollPane();
		scroll.setViewportView(listPluginSupplement3);
		listPanel.add(scroll);
		phasePanel.add(listPanel);
		
		workflowPanel.add(phasePanel);
		
		/////////////////////////////////////////////////////////////////////////////////////
		
		JPanel controlPanel = new JPanel(new GridLayout(4,1));
		controlPanel.setBorder(BorderFactory.createTitledBorder("Workflow Control"));
		buttonActivate = new JButton("Activate the workflow");
		buttonAnalysis = new JButton("Analyze Text");
		buttonReset = new JButton("Close the workflow");
		
		JPanel threadPanel = new JPanel(new GridLayout(1,1));
		radioMultiThread = new JRadioButton("Mutli-thread Mode", true);
		radioSingleThread = new JRadioButton("Single-thread Mode", false);
		threadPanel.setBorder(BorderFactory.createTitledBorder("Thread Mode"));
		threadPanel.add(radioMultiThread);
		threadPanel.add(radioSingleThread);
		ButtonGroup groupThread = new ButtonGroup();
		groupThread.add(radioMultiThread);
		groupThread.add(radioSingleThread);
		
		controlPanel.add(threadPanel);
		controlPanel.add(buttonActivate);
		controlPanel.add(buttonAnalysis);
		controlPanel.add(buttonReset);
		
		buttonAnalysis.setEnabled(false);
		buttonReset.setEnabled(false);
		
		/////////////////////////////////////////////////////////////////////////////////////
		
		JPanel pluginInfoPanel = new JPanel(new GridLayout(1,1));
		pluginInfoPanel.setBorder(BorderFactory.createTitledBorder("Plug-in Information"));
		areaPluginInfo = new JTextArea();
		areaPluginInfo.setLineWrap(true);
		scroll = new JScrollPane();
		scroll.setViewportView(areaPluginInfo);
		pluginInfoPanel.add(scroll);
		
		JPanel infoPanel = new JPanel(new GridLayout(1,2));
		infoPanel.add(pluginInfoPanel);
		infoPanel.add(controlPanel);
		
		JPanel panel = new JPanel(new GridLayout(2, 1));
		workflowPanel.setBorder(BorderFactory.createTitledBorder("HanNanum Workflow"));
		panel.add(workflowPanel);
		panel.add(infoPanel);

        return panel;
	}

	
	private JComponent createPaneCenter() {
		splitPaneBottom = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		JPanel panel = new JPanel(new GridLayout(1,1));
		panel.setBorder(BorderFactory.createTitledBorder("Input Text"));
		inputTextArea = new JTextArea();
		JScrollPane scroll = new JScrollPane();
		scroll.setViewportView(inputTextArea);
		panel.add(scroll);
		splitPaneBottom.setLeftComponent(panel);
	    
	    panel = new JPanel(new GridLayout(1,1));
		panel.setBorder(BorderFactory.createTitledBorder("Result"));
		outputTextArea = new JTextArea();
		scroll = new JScrollPane();
		scroll.setViewportView(outputTextArea);
		panel.add(scroll);
		splitPaneBottom.setRightComponent(panel);
		
		splitPaneBottom.setOneTouchExpandable(true);
	    
		return splitPaneBottom;
	}
	
	private class SharedActionHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			try {
				Object source = e.getSource();

				if (source == menuItemFileOpen) {
					fileOpen();
				}
				else if (source == buttonActivate) {
					initWorkflow();
					activateWorkflow();
				}
				else if (source == buttonAnalysis) {
					analyzeText();
				}
				else if (source == buttonReset) {
					reset();
				} else if (source == radioMultiThread) {
					multiThreadMode = true;
				} else if (source == radioSingleThread) {
					multiThreadMode = false;
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
		
		private void fileOpen() {
			JFileChooser chooser = new JFileChooser();
			
			chooser.setCurrentDirectory(new java.io.File("."));
			chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			chooser.setAcceptAllFileFilterUsed(false);
			
			if (chooser.showOpenDialog(mainFrame) == JFileChooser.APPROVE_OPTION) {
				File selectedFile = chooser.getSelectedFile();
				
				try {
					BufferedReader br = new BufferedReader(new FileReader(selectedFile));
					String line = null;
					while ((line = br.readLine()) != null) {
						inputTextArea.append(line + "\n");
					}
					br.close();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		private void initWorkflow() {
			/* 워크플로 초기화 */
			workflow.clear();

			/* 설정한 워크플로 활성화 */
			String pluginName = null;
			
			// phase1 plugin
			for (int i = 0; i < listModelSupplement1.size(); i++) {
				pluginName = (String)listModelSupplement1.get(i);
				if (pluginName.equals("InformalSentenceFilter")) {
					workflow.appendPlainTextProcessor(new InformalSentenceFilter(), null);
				} else if (pluginName.equals("SentenceSegmentor")) {
					workflow.appendPlainTextProcessor(new SentenceSegmentor(), null);
				}
			}
			
			// phase2 plugin
			if (listModelMajor2.size() > 0) {
				pluginName = (String)listModelMajor2.get(0);
				if (pluginName.equals("ChartMorphAnalyzer")) {
					workflow.setMorphAnalyzer(new ChartMorphAnalyzer(), "conf/plugin/MajorPlugin/MorphAnalyzer/ChartMorphAnalyzer.json");
				}
			} else {
				return;
			}
			
			for (int i = 0; i < listModelSupplement2.size(); i++) {
				pluginName = (String)listModelSupplement2.get(i);
				if (pluginName.equals("UnknownMorphProcessor")) {
					workflow.appendMorphemeProcessor(new UnknownProcessor(), null);
				} else if (pluginName.equals("KaistTagMappingLevel1")) {
					workflow.appendMorphemeProcessor(new KaistTagMappingLevel1(), null);
				} else if (pluginName.equals("KaistTagMappingLevel2")) {
					workflow.appendMorphemeProcessor(new KaistTagMappingLevel2(), null);
				} else if (pluginName.equals("KaistTagMappingLevel3")) {
					workflow.appendMorphemeProcessor(new KaistTagMappingLevel3(), null);
				} else if (pluginName.equals("KaistTagMappingLevel4")) {
					workflow.appendMorphemeProcessor(new KaistTagMappingLevel4(), null);
				} 
			}
			
			// phase3 plugin
			if (listModelMajor3.size() > 0) {
				pluginName = (String)listModelMajor3.get(0);
				if (pluginName.equals("HmmPosTagger")) {
					workflow.setPosTagger(new HMMTagger(), "conf/plugin/MajorPlugin/PosTagger/HmmPosTagger.json");
				}
			} else {
				return;
			}
			for (int i = 0; i < listModelSupplement3.size(); i++) {
				pluginName = (String)listModelSupplement3.get(i);
				if (pluginName.equals("NounExtractor")) {
					workflow.appendPosProcessor(new NounExtractor(), null);
				} 
			}
		}
		
		private void activateWorkflow() {
			try {
				workflow.activateWorkflow(multiThreadMode);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			buttonAnalysis.setEnabled(true);
			buttonReset.setEnabled(true);
			buttonActivate.setEnabled(false);
			radioMultiThread.setEnabled(false);
			radioSingleThread.setEnabled(false);
			listPluginSupplement1.setEnabled(false);
			listPluginSupplement2.setEnabled(false);
			listPluginSupplement3.setEnabled(false);
			listPluginMajor2.setEnabled(false);
			listPluginMajor3.setEnabled(false);
			
			activated = true;
		}
		
		private void analyzeText() {
			String text = inputTextArea.getText();
			
			if (text != null && text.length() > 0) {
				workflow.analyze(inputTextArea.getText());
				outputTextArea.setText(workflow.getResultOfDocument());
				
				buttonReset.setEnabled(true);
				buttonActivate.setEnabled(false);
			} else {
				outputTextArea.setText("");
			}
		}
		
		private void reset() {
			workflow.clear();
			
			buttonActivate.setEnabled(true);
			buttonAnalysis.setEnabled(false);
			buttonReset.setEnabled(false);
			radioMultiThread.setEnabled(true);
			radioSingleThread.setEnabled(true);
			listPluginSupplement1.setEnabled(true);
			listPluginSupplement2.setEnabled(true);
			listPluginSupplement3.setEnabled(true);
			listPluginMajor2.setEnabled(true);
			listPluginMajor3.setEnabled(true);
			
			activated = false;
		}
	}
	
	private class PluginTransferHandler extends TransferHandler {
		private static final long serialVersionUID = 1L;
		private int phase = 0;
		private int type = 0;
		
		public PluginTransferHandler(int phase, int type) {
			this.phase = phase;
			this.type = type;
		}
		
        public boolean canImport(TransferHandler.TransferSupport info) {
        	if (!activated && selectedPlugin != null && phase == selectedPlugin.phase && type == selectedPlugin.type) {
        		return true;
        	}
            return false;
        }

        public boolean importData(TransferHandler.TransferSupport info) {
            if (!info.isDrop()) {
                return false;
            }
            
            if (!info.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                return false;
            }
            
            JList.DropLocation dl = (JList.DropLocation)info.getDropLocation();
            DefaultListModel listModel = null;
            int index = dl.getIndex();
            
            switch (selectedPlugin.phase) {
            case PluginInfo.PHASE1:
            	listModel = (DefaultListModel)listPluginSupplement1.getModel();
            	break;
            case PluginInfo.PHASE2:
            	if (selectedPlugin.type == PluginInfo.MAJOR) {
            		listModel = (DefaultListModel)listPluginMajor2.getModel();
            		listModel.clear();
            		index = 0;
            	} else if (selectedPlugin.type == PluginInfo.SUPPLEMENT) {
            		listModel = (DefaultListModel)listPluginSupplement2.getModel();
            	}
            	break;
            case PluginInfo.PHASE3:
            	if (selectedPlugin.type == PluginInfo.MAJOR) {
            		listModel = (DefaultListModel)listPluginMajor3.getModel();
            		listModel.clear();
            		index = 0;
            	} else if (selectedPlugin.type == PluginInfo.SUPPLEMENT) {
            		listModel = (DefaultListModel)listPluginSupplement3.getModel();
            	}
            	break;
            }
            
            if (listModel == null) {
            	return false;
            }
            
            try {
            	Transferable t = info.getTransferable();
                String data = (String)t.getTransferData(DataFlavor.stringFlavor);
                int pIndex = listModel.indexOf(data);
                
                listModel.add(index, data);

                if (pIndex != -1) {
	                if (pIndex >= index) {
	                	pIndex++;
	                }
	                listModel.remove(pIndex);
                }
            } catch (Exception e) {
            	return false;
            }
            
            return false;
        }
        
        public int getSourceActions(JComponent c) {
            return COPY;
        }
        
        protected Transferable createTransferable(JComponent c) {
            JList list = (JList)c;
            Object[] values = list.getSelectedValues();
    
            StringBuffer buff = new StringBuffer();

            if (values.length >= 0) {
                Object val = values[0];
                buff.append(val);
            }
            
            tempPlugin.name = buff.toString();
            tempPlugin.phase = phase;
            tempPlugin.type = type;
            selectedPlugin = tempPlugin;
            
            return new StringSelection(buff.toString());
        }
    }
}
