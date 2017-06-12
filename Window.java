import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import java.util.*;
public class Window implements ActionListener {
	//JObjetos
	private JFrame frame;
	private JPanel entrada,testeo,mainPanel,out,panel; 
	private JButton setear,agregar,reset,inputWord;
	private JTabbedPane tab;
	private JLabel eti;
	private JTextField field, word;
	private ArrayList<JTextField> aFields = new ArrayList<JTextField>();
	private ArrayList<JLabel> aLabels = new ArrayList<JLabel>();
	//Strings
	private String tran,first,second,toSearch,estadoIni,estadoFin,act,estadoAct,blank="p";
	private String[] cinta,elementos;
	private int contFields =2,contLabel=1, posCinta;
	private Hashtable<String, String> hash = new Hashtable<String, String>();
	public Window() {
		frame = new JFrame();
		tab = new JTabbedPane();
		//Se Inicializan los paneles
		entrada  = setEntrada();
		testeo = setTesteo();
		//Se añaden los paneles anteriores como pestañas
		tab.add("Entrada", entrada);
		tab.add("Testeo",testeo);
		//Configuración básica del frame principal
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
		//Panel Superior
			JPanel p0 = new JPanel();
			p0.setLayout(new FlowLayout());
			p0.add(new JLabel("Ingrese:"));
		//Panel Superior-2
			JPanel p1 = new JPanel();
			p1.setLayout (new FlowLayout());
			//Labels y Fields para los inputs
			p1.add(new JLabel("Estado Inicial "));
			JTextField box = new JTextField(5);
			p1.add (box);
			p1.add(new JLabel("Estado Final "));
			JTextField box2 = new JTextField(5);
			p1.add (box2);
		//Panel textos (Instructivo de ejemplo)
			JPanel text = new JPanel(new BorderLayout());
			text.add(new JLabel("<html> >Transiciones: <br> Ejemplos<br>     δ(q₁,a)-->(q₂,A,D)<br>     δ(q₂,b)-->(q₃,B,I)</html>"),BorderLayout.WEST);
			text.add(new JLabel(" Se define el caracter blanco como: "+blank),BorderLayout.EAST);
		//Se agregan al arrayList los textFields para su posterior lectura
		aFields.add(box);
		aFields.add(box2);
		//Panel Main (Input de Transiciones)
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
		panel.add(text);
		panel.add(mainPanel);
		panel.add(p2);
		return panel;
	}
	public JPanel setTesteo(){
		panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		//Panel de Input
			JPanel input = new JPanel(new FlowLayout());
			word = new JTextField(10);
			inputWord= new JButton("Evaluar");
			inputWord.addActionListener(this);
			input.add(new JLabel("Ingrese palabra a probar"));
			input.add(word);
			input.add(inputWord);
		//Panel de Salida
			out = new JPanel();
			out.setLayout(new BoxLayout(out, BoxLayout.Y_AXIS));
		//Se Agregan los paneles
		panel.add(input);
		panel.add(out);
		return panel;
	}
	
	public void actionPerformed(ActionEvent e) {
		JButton pressed = ((JButton) e.getSource());
		if(pressed==agregar){ 			//Añade textFields para agregar Transiciones
			mainPanel.add(new subPanel());
			frame.pack();
		}else if(pressed==setear){ 		//Guarda las transiciones ingresadas
			hash.clear();//limpia las transiciones anteriores
			registraT();
			if(!hash.isEmpty()){
				JOptionPane.showMessageDialog(frame,
					    "Dirígase a la pestaña de >Testeo< ");
			}else{
				JOptionPane.showMessageDialog(frame,
					    "Debe ingresar los estados y las transiciones");
			}
			
		}else if(pressed==inputWord){   //Registra la palabra a testear
			if((estadoFin=="" || estadoIni=="" || hash.isEmpty())){
				JOptionPane.showMessageDialog(frame,
					    "Debe ingresar los estados y las transiciones");
			}else{
				toSearch=word.getText();
				posCinta=0; //Se reinicia el cabezal a la posición inicial
				tryMT(toSearch);
			}
		}else {//Reset
			frame.dispose();
			@SuppressWarnings("unused")
			Window one= new Window();
		}
	}
	public void registraT(){//Registra los textFields del Panel de entrada
		estadoIni =((JTextField) aFields.get(0)).getText();
		estadoFin =((JTextField) aFields.get(1)).getText();
		//registra los campos de transiciones de par en par 
		for(int i=2;hash.size()<((contFields-2)/2);i=i+2){
			first=((JTextField) aFields.get(i)).getText();
			second =((JTextField) aFields.get(i+1)).getText();
			hash.put(first,second); 
		}
	}
	public void tryMT(String toSearch){
		String toShow = toSearch;
		toSearch=toSearch.concat(blank);//se agrega el caracter blanco al final de la palabra
		cinta = toSearch.split("(?!^)");//Se separa la palabra de entrada caracter a caracter
		//Se comprueba el ingreso de al menos una transicion
		if(!hash.isEmpty()){
			tryTrans(); //Se prueban las transiciones a la palabra ingresada
			if(estadoAct.equals(estadoFin) ){
				out.add(new subPanel(toShow+":    Se Acepta la palabra"));
			}else{
				out.add(new subPanel(toShow+":    Se Rechaza. No Existe Transición: "+(act)));
			}
			if(contLabel<=(2*hash.size())){
				frame.pack();
			}
		}
	}
	public void tryTrans(){
		boolean bool = true;
		estadoAct = estadoIni;
		while(bool){
			act = cinta[posCinta];
			act = (estadoAct.concat(",")).concat(act);
			//Busca la transición dentro del hashTable
			tran = (String) hash.get(act);
			if(tran!=null){
				//se guarda la transición (q1,a,D) como arreglo [q1,a,D]
				elementos = tran.split(",");
				estadoAct = elementos[0];
				cinta[posCinta]=elementos[1];
				//se desplaza el cabezal de la cinta
				if((elementos[2]).equals("D")){
					posCinta++;
				}else if((elementos[2]).equals("I")){
					posCinta--;
				}
			}else{
				bool=false;
			}
			//Si se logra llegar al estado final, se termina el ciclo.
			if(estadoAct.equals(estadoFin)){
				break;
			}
		}
	}
	@SuppressWarnings("serial")
	//Añade "subPaneles" a los paneles anteriormente creados
	private class subPanel extends JPanel{
		subPanel sPanel;
		public subPanel(){// Para las transiciones
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
			//System.out.println(">     cont:"+contFields);
			//A cada input de transicion se le añade un boton de borrar
			JButton removeMe = new JButton("Eliminar Transicion");
			removeMe.setFont(new Font("Arial", Font.PLAIN, 15));
	        removeMe.addActionListener(new ActionListener(){
	        	//Para encontrar qué panel llamó al botón
	                public void actionPerformed(ActionEvent e) {
	                    sPanel.getParent().remove(sPanel);
						Iterator<JTextField> it = aFields.iterator();
						while (it.hasNext()) {
						  JTextField p = it.next();
						  if (p.getParent().equals(sPanel)) {
						    it.remove();
						  }
						}
	                    contFields=contFields-2;
	                    frame.pack();
	                }
	        });
	        add(removeMe);
		}
		public subPanel(String toShow){ // Para los outputs de Validacion
			sPanel = this;
			sPanel.setLayout(new FlowLayout());
			eti = new JLabel(contLabel+") "+toShow);
			//Para las primeras etiquetas se crean, guardan y muestran
			if(contLabel<=(2*hash.size())){
				aLabels.add(eti);
				sPanel.add(eti);
			}else{//Cuando son muchas, se comienzan a reescribir las etiquetas
				((JLabel)(aLabels.get(contLabel%(2*hash.size())))).setText(contLabel+") "+toShow);
			}
			contLabel++;
		}
	}
}
