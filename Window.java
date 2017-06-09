import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import java.util.*;
public class Window implements ActionListener {
	//JObjetos
	private JFrame frame;
	private JPanel entrada,test,mainPanel,out,panel; 
	private JButton setear,agregar,reset,in;
	private JTabbedPane tab;
	private JLabel eti;
	private JTextField field, word;
	private ArrayList<JTextField> aFields = new ArrayList<JTextField>();
	private ArrayList<JLabel> aLabels = new ArrayList<JLabel>();
	//Strings
	private String tran,first,second,toSearch,eIni,eFin,act,eAct,blank="p";
	private String[] cinta;
	private int contFields =2,conLabel=1, posCinta;
	private Hashtable<String, String> hash = new Hashtable<String, String>();;
	//FALTA:
	//Ajustar cantidad de texto a mostrar en out.
	public Window() {
		frame = new JFrame();
		tab = new JTabbedPane();
		//Seteo de pestañas
		entrada  = setEntrada();
		test = setTest();
		//Se añaden paneles a pestañas
		tab.add("Entrada", entrada);
		tab.add("Testeo",test);
		
		frame.getContentPane().add(tab);
		frame.setTitle("Máquina de Turing");
		frame.pack();
	    frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		
	}
	public JPanel setEntrada(){
		panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		//Panel top
			JPanel p0 = new JPanel();
			p0.setLayout(new FlowLayout());
			p0.add(new JLabel("Ingrese:"));
		//Panel Superior
			JPanel p1 = new JPanel();
			p1.setLayout (new FlowLayout());
			//Labels y Fields
			p1.add(new JLabel("Estado Inicial "));
			JTextField box = new JTextField(5);
			p1.add (box);
			p1.add(new JLabel("Estado Final "));
			JTextField box2 = new JTextField(5);
			p1.add (box2);
		//Panel textos
			JPanel side = new JPanel(new FlowLayout());
			side.add(new JLabel("<html> >Transiciones: <br> Ejemplos<br>     δ(q₁,a)-->(q₂,A,D)<br>     δ(q₂,b)-->(q₃,B,I)</html>"),BorderLayout.WEST);
			side.add(new JLabel("                       Se define el caracter blanco como: "+blank),BorderLayout.CENTER);
			/*JTextField box3 = new JTextField(1);
			side.add (box3);*/
		//Se agregan al arrayList los textFields
		aFields.add(box);
		aFields.add(box2);
		/*aFields.add(box3);*/
		//Panel Main
			mainPanel = new JPanel();
			mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));	
		//Panel inferior (Botones)
			JPanel p2 = new JPanel();
			p2.setLayout (new FlowLayout());
			setear = new JButton("Guardar Configuración");
			setear.addActionListener(this);
			agregar = new JButton("Añadir transición");
			agregar.addActionListener(this);
			reset = new JButton("Reset");
			reset.addActionListener(this);
			p2.add(agregar);
			p2.add(setear);
			p2.add(reset);		
		//Añadir paneles en orden
	    panel.add(p0);
		panel.add(p1);
		panel.add(side);
		panel.add(mainPanel);
		panel.add(p2);
		return panel;
	}
	public JPanel setTest(){
		panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		//Panel de Input
			JPanel input = new JPanel(new FlowLayout());
			word = new JTextField(10);
			in = new JButton("Evaluar");
			in.addActionListener(this);
			input.add(new JLabel("Ingrese palabra a probar"));
			input.add(word);
			input.add(in);
		//Panel de Salida
			out = new JPanel();
			out.setLayout(new BoxLayout(out, BoxLayout.Y_AXIS));
		//Se Agregan los paneles
		panel.add(input);
		panel.add(out);
		return panel;
	}
	public void actionPerformed(ActionEvent e) {
		JButton press = ((JButton) e.getSource());
		if(press==agregar){
			//Añade inputs de Transiciones
			mainPanel.add(new subPanel());
			frame.pack();
		}else if(press==setear){
			//Setea las transiciones
			hash.clear();
			System.out.println(" tamaño inicial: "+hash.size());
			registraT();
		}else if(press==in){
			//Registra la palabra a testear
			toSearch=word.getText();
			posCinta=0; //Se reinicia posición inicial
			System.out.println("Se ingresó :"+toSearch);
			tryMT(toSearch);
		}else {//Reset
			frame.dispose();
			@SuppressWarnings("unused")
			Window one= new Window();
		}
	}
	public void registraT(){
		eIni =((JTextField) aFields.get(0)).getText();
		System.out.println("ini:"+eIni);
		eFin =((JTextField) aFields.get(1)).getText();
		System.out.println("fin:"+eFin);
		System.out.println("contador:"+contFields);
		for(int i=2;hash.size()<((contFields-2)/2);i=i+2){
			System.out.print("iBefore: "+i+"  ");
			first=((JTextField) aFields.get(i)).getText();
			System.out.print("delta: "+first);
			second =((JTextField) aFields.get(i+1)).getText();
			System.out.println("->"+second+"|");
			hash.put(first,second); 
			System.out.println("> tamaño :"+hash.size());
		}
	}
	public void tryMT(String toSearch){
		String toShow = toSearch;
		toSearch=toSearch.concat(blank);
		cinta = toSearch.split("(?!^)");
		if(!hash.isEmpty()){
			tryTrans();
			if(eAct.equals(eFin) ){
				out.add(new subPanel(toShow+":    Se Acepta la palabra"));
			}else{
				out.add(new subPanel(toShow+":    Se Rechaza. No Existe Transición:"+(act)));
			}
			if(conLabel<=(2*hash.size())){
				frame.pack();
			}
			
		}
		else{
			out.add(new subPanel("Primero defina el autómata"));
		}
	}
	public void tryTrans(){
		boolean bool = true;
		eAct = eIni;
		while(bool){
			act = cinta[posCinta];
			act = (eAct.concat(",")).concat(act);
			System.out.print(" Estado actual: "+act+" | ");
			tran = (String) hash.get(act);
			System.out.println("transición:"+tran);
			if(tran!=null){
				eAct = tran.substring(0,1);
				System.out.println("Estado Actual: "+eAct);
				cinta[posCinta]=tran.substring(2,3);
				if((tran.substring(4,5)).equals("D")){
					posCinta++;
				}else if((tran.substring(4,5)).equals("I")){
					posCinta--;
				}
			}else{
				bool=false;
			}
			if(eAct.equals(eFin)){
				break;
			}
		}
	}
	@SuppressWarnings("serial")
	private class subPanel extends JPanel{
		subPanel sPanel;
		public subPanel(){
			super();
			sPanel = this;
			sPanel.setLayout(new FlowLayout());
			sPanel.add(new JLabel("δ("));
			field = new JTextField(3);
			aFields.add(field);
			sPanel.add(field);
			sPanel.add(new JLabel(")-->("));
			field=new JTextField(4);
			aFields.add(field);
			sPanel.add(field);
			sPanel.add(new JLabel(")"));
			contFields=contFields+2;
			System.out.println(">     cont:"+contFields);
			JButton removeMe = new JButton("Eliminar Transicion");
			removeMe.setFont(new Font("Arial", Font.PLAIN, 15));
	        removeMe.addActionListener(new ActionListener(){
	                public void actionPerformed(ActionEvent e) {
	                    sPanel.getParent().remove(sPanel);
	                    System.out.print("size b: "+aFields.size());


						Iterator<JTextField> it = aFields.iterator();
						while (it.hasNext()) {
						  JTextField p = it.next();
						  if (p.getParent().equals(sPanel)) {
						    it.remove();
						  }
						}
						

	                    System.out.println(" a: "+aFields.size());
	                    contFields=contFields-2;
	                    frame.pack();
	                }
	        });
	        add(removeMe);
		}
		public subPanel(String toShow){
			sPanel = this;
			sPanel.setLayout(new FlowLayout());
			eti = new JLabel(conLabel+") "+toShow);
			if(conLabel<=(2*hash.size())){
				aLabels.add(eti);
				sPanel.add(eti);
			}else{
				((JLabel)(aLabels.get(conLabel%(2*hash.size())))).setText(conLabel+") "+toShow);
			}
			conLabel++;
			
		}
	}
}
