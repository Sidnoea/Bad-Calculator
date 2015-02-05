import java.awt.Font;
import java.awt.GridLayout;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import java.lang.Math;

public class BadCalculator extends JFrame {
	
	int numButtons = 19;
	int rows = 5, cols = 4;
	String[] ops = {"0", "0"};
	String result = "", operator = "";
	int curOp = 0;
	
	String[] funcs = {"=", "C", "%", "+", "-", "*", "neg", ".", "/"};
	final int EQUALS = 0, CLEAR = 1, MODULO = 2, ADD = 6, SUB = 10, MULT = 14, NEG = 15, DOT = 17, DIV = 18;
	final int[] NUMBUTTONS = {16, 3, 4, 5, 7, 8, 9, 11, 12, 13};
	
	CalcButton[] buttons = new CalcButton[rows*cols - 1];
	CalcTextArea textBox = new CalcTextArea();

	public static void main(String[] args) {
		
		SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new BadCalculator();
            }
        });
		
	}
	
	private BadCalculator() {
		
		super("Bad Calculator");
		launch();
	}
	
	abstract class CalcButton extends JButton {
		
		private CalcButton() {
			setFont(new Font(getFont().getClass().getName(), Font.PLAIN, 30));
		}
		
		abstract void action();
		
	}
	
	private class NumButton extends CalcButton {
		
		public Integer num;
		
		public NumButton(int num) {
			
			this.num = num;
			setText(this.num.toString());
		}
		
		public void action() {
			if(result != "") {
				buttons[CLEAR].action();
			}
			if(ops[curOp].equals("0")) {
				ops[curOp] = num.toString();
			}
			else if(ops[curOp].equals("-0")) {
				ops[curOp] = "-" + num.toString();
			}
			else {
				ops[curOp] += num.toString();
			}
			textBox.refresh();
		}
	}
	
	private class FuncButton extends CalcButton {
		
		public String func;
		
		public FuncButton(String func) {
			
			this.func = func;
			setText(func);
		}
		
		public void action() {
			
			if(func == "=") {
				if(operator != "") {
					double val0 = Double.valueOf(ops[0]);
					double val1 = Double.valueOf(ops[1]);
					double temp = 0.0;
					
					switch (operator) {
					case "+":
						temp = val0 + val1;
						break;
					case "-":
						temp = val0 - val1;
						break;
					case "*":
						temp = val0 * val1;
						break;
					case "/":
						temp = val0 / val1;
						break;
					case "%":
						temp = val0 % val1;
						break;
					}
					result = String.valueOf(badMath(temp));
					if(result.substring(result.length() - 2).equals(".0")) {
						result = result.substring(0, result.length() - 2);
					}
				}
			}
			else if(func == "C") {
				ops[0] = "0";
				ops[1] = "0";
				operator = "";
				result = "";
				curOp = 0;
			}
			else if(func == "neg") {
				if(result != "") {
					String temp;
					if(result.charAt(0) == '-') {
						temp = result.substring(1);
					}
					else {
						temp = "-" + result;
						if(temp.equals("-0")) {
							temp = "0";
						}
					}
					ops[0] = result;
					ops[1] = "-1";
					operator = "*";
					result = String.valueOf(badMath(Double.valueOf(temp)));
					if(result.substring(result.length() - 2).equals(".0")) {
						result = result.substring(0, result.length() - 2);
					}
				}
				else {
					if(ops[curOp].charAt(0) == '-') {
						ops[curOp] = ops[curOp].substring(1);
					}
					else {
						ops[curOp] = "-" + ops[curOp];
					}
				}
			}
			else if(func == ".") {
				if(result == "") {
					if(ops[curOp].indexOf(".") == -1) {
						ops[curOp] += ".";
					}
				}
			}
			else {  //addition, subtraction, etc.
				if(curOp == 0) {
					operator = func;
					curOp = 1;
				}
				else {
					if(result == "") {
						buttons[EQUALS].action();
					}
					ops[0] = result;
					ops[1] = "0";
					operator = func;
					result = "";
					curOp = 1;
				}
			}
			textBox.refresh();
		}
	}
	
	private class CalcTextArea extends JTextArea {
		
		private CalcTextArea() {
			
			refresh();
		}
		
		public void refresh() {
			
			setText(ops[0]);
			if(curOp != 0) {
				append(" " + operator + " " + ops[1]);
			}
			if(result != "") {
				append(" =\n" + result);
			}
		}
	}
	
	private double badMath(Double num) {
	    
	    double ans = num + (Math.pow(-1, Math.floor(num % 2)))*(num % 10 + 1)/25*num;
	    
	    if(Math.floor(num) == num) {
	    	ans = Math.floor(ans);
	    }
	    if(ans == num) {
	        ans += Math.pow(-1, Math.floor(num % 2))*7;
	    }
	    return Math.round(ans*1.0e12)/1.0e12;
	}
	
	private void launch() {
		
		int height = Toolkit.getDefaultToolkit().getScreenSize().height;
        int width  = Toolkit.getDefaultToolkit().getScreenSize().width ;
        
        setBounds(width / 2 - 300, height / 2 - 300, 600, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        setLayout(new GridLayout(rows, cols));

        int curButton = 0;
        int curFunc = 0;
        textBox.setEditable(false);
        add(textBox);
        while(curButton < 3) {
        	add(buttons[curButton++] = new FuncButton(funcs[curFunc++]));
        }
        for(int i = 3; i < 6; i++) {
        	for(int j = 1; j < 4; j++) {
        		add(buttons[curButton++] = new NumButton(3*(i-3)+j));
        	}
        	add(buttons[curButton++] = new FuncButton(funcs[curFunc++]));
        }
        add(buttons[curButton++] = new FuncButton(funcs[curFunc++]));
        add(buttons[curButton++] = new NumButton(0));
        add(buttons[curButton++] = new FuncButton(funcs[curFunc++]));
        add(buttons[curButton++] = new FuncButton(funcs[curFunc++]));
        
        for(CalcButton button : buttons) {
    		button.addActionListener(new ActionListener() {
    			public void actionPerformed(ActionEvent event) {
    				((CalcButton)event.getSource()).action();
    			}
    		});
        }
        
        KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        manager.addKeyEventDispatcher(new KeyEventDispatcher() {

			public boolean dispatchKeyEvent(KeyEvent e) {
				
				if(e.getID() == KeyEvent.KEY_PRESSED) {
					System.out.println(e.getKeyCode() + " " + e.getKeyChar());
					switch(e.getKeyCode()) {
					case KeyEvent.VK_0:
					case KeyEvent.VK_NUMPAD0:
						buttons[NUMBUTTONS[0]].action();
						break;
					case KeyEvent.VK_1:
					case KeyEvent.VK_NUMPAD1:
						buttons[NUMBUTTONS[1]].action();
						break;
					case KeyEvent.VK_2:
					case KeyEvent.VK_NUMPAD2:
						buttons[NUMBUTTONS[2]].action();
						break;
					case KeyEvent.VK_3:
					case KeyEvent.VK_NUMPAD3:
						buttons[NUMBUTTONS[3]].action();
						break;
					case KeyEvent.VK_4:
					case KeyEvent.VK_NUMPAD4:
						buttons[NUMBUTTONS[4]].action();
						break;
					case KeyEvent.VK_5:
					case KeyEvent.VK_NUMPAD5:
						buttons[NUMBUTTONS[5]].action();
						break;
					case KeyEvent.VK_6:
					case KeyEvent.VK_NUMPAD6:
						buttons[NUMBUTTONS[6]].action();
						break;
					case KeyEvent.VK_7:
					case KeyEvent.VK_NUMPAD7:
						buttons[NUMBUTTONS[7]].action();
						break;
					case KeyEvent.VK_8:
					case KeyEvent.VK_NUMPAD8:
						buttons[NUMBUTTONS[8]].action();
						break;
					case KeyEvent.VK_9:
					case KeyEvent.VK_NUMPAD9:
						buttons[NUMBUTTONS[9]].action();
						break;
					case KeyEvent.VK_ENTER:
					case KeyEvent.VK_EQUALS:
						buttons[EQUALS].action();
						break;
					case KeyEvent.VK_ADD:
					case KeyEvent.VK_PLUS:
						buttons[ADD].action();
						break;
					case KeyEvent.VK_SUBTRACT:
					case KeyEvent.VK_MINUS:
						buttons[SUB].action();
						break;
					case KeyEvent.VK_MULTIPLY:
					case KeyEvent.VK_ASTERISK:
						buttons[MULT].action();
						break;
					case KeyEvent.VK_DIVIDE:
					case KeyEvent.VK_SLASH:
						buttons[DIV].action();
						break;
					case KeyEvent.VK_DECIMAL:
					case KeyEvent.VK_PERIOD:
						buttons[DOT].action();
					}
				}
				
				return false;
			}
        	
        });
        
        setVisible(true);
	}
}
