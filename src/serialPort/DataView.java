package serialPort;

import java.awt.Button;
import java.awt.Choice;
import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Label;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.Timer;
import javax.swing.JOptionPane;

import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import serialException.*;

/**
 * ���������ʾ��
 * @author ljb
 *
 */
public class DataView extends Frame {
		
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	Client client = null;

	private List<String> commList = null;	//������ö˿ں�
	private SerialPort serialPort = null;	//���洮�ڶ���
	
	private Font font = new Font("΢���ź�", Font.BOLD, 25);
	
	private Label tem = new Label("��������", Label.CENTER);	//�¶�
	private Label hum = new Label("��������", Label.CENTER);	//ʪ��
	private Label pa = new Label("��������", Label.CENTER);	//ѹǿ
	private Label rain = new Label("��������", Label.CENTER);	//����

	
	private Choice commChoice = new Choice();	//����ѡ��������
	private Choice bpsChoice = new Choice();	//������ѡ��
	
	private Button openSerialButton = new Button("�򿪴���");
	
	Image offScreen = null;	//�ػ�ʱ�Ļ���
	
	//����window��icon
	Toolkit toolKit = getToolkit();
	Image icon = toolKit.getImage(DataView.class.getResource("computer.png"));

	/**
	 * ��Ĺ��췽��
	 * @param client
	 */
	public DataView(Client client) {
		this.client = client;
		commList = SerialTool.findPort();	//�����ʼ��ʱ��ɨ��һ����Ч����
	}
	
	/**
	 * ���˵�������ʾ��
	 * ���Label����ť��������������¼�������
	 */
	@SuppressWarnings("static-access")
	public void dataFrame() {
		this.setBounds(client.LOC_X, client.LOC_Y, client.WIDTH, client.HEIGHT);
		this.setTitle("����ˮ�ʼ��ϵͳ");
		this.setIconImage(icon);
		this.setBackground(Color.white);
		this.setLayout(null);
		
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent arg0) {
				if (serialPort != null) {
					//�����˳�ʱ�رմ����ͷ���Դ
					SerialTool.closePort(serialPort);
				}
				System.exit(0);
			}
			
		});
		
		tem.setBounds(140, 103, 225, 50);
		tem.setBackground(Color.black);
		tem.setFont(font);
		tem.setForeground(Color.white);
		add(tem);
		
		hum.setBounds(520, 103, 225, 50);
		hum.setBackground(Color.black);
		hum.setFont(font);
		hum.setForeground(Color.white);
		add(hum);
		
		pa.setBounds(140, 193, 225, 50);
		pa.setBackground(Color.black);
		pa.setFont(font);
		pa.setForeground(Color.white);
		add(pa);

		rain.setBounds(520, 193, 225, 50);
		rain.setBackground(Color.black);
		rain.setFont(font);
		rain.setForeground(Color.white);
		add(rain);
		
		
		
		//��Ӵ���ѡ��ѡ��
		commChoice.setBounds(160, 397, 200, 200);
		//����Ƿ��п��ô��ڣ��������ѡ����
		if (commList == null || commList.size()<1) {
			JOptionPane.showMessageDialog(null, "û����������Ч���ڣ�", "����", JOptionPane.INFORMATION_MESSAGE);
		}
		else {
			for (String s : commList) {
				commChoice.add(s);
			}
		}
		add(commChoice);
		
		//��Ӳ�����ѡ��
		bpsChoice.setBounds(526, 396, 200, 200);
		bpsChoice.add("1200");
		bpsChoice.add("2400");
		bpsChoice.add("4800");
		bpsChoice.add("9600");
		bpsChoice.add("14400");
		bpsChoice.add("19200");
		bpsChoice.add("115200");
		add(bpsChoice);
		
		//��Ӵ򿪴��ڰ�ť
		openSerialButton.setBounds(250, 490, 300, 50);
		openSerialButton.setBackground(Color.lightGray);
		openSerialButton.setFont(new Font("΢���ź�", Font.BOLD, 20));
		openSerialButton.setForeground(Color.darkGray);
		add(openSerialButton);
		//��Ӵ򿪴��ڰ�ť���¼�����
		openSerialButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				
				//��ȡ��������
				String commName = commChoice.getSelectedItem();			
				//��ȡ������
				String bpsStr = bpsChoice.getSelectedItem();
				
				//��鴮�������Ƿ��ȡ��ȷ
				if (commName == null || commName.equals("")) {
					JOptionPane.showMessageDialog(null, "û����������Ч���ڣ�", "����", JOptionPane.INFORMATION_MESSAGE);			
				}
				else {
					//��鲨�����Ƿ��ȡ��ȷ
					if (bpsStr == null || bpsStr.equals("")) {
						JOptionPane.showMessageDialog(null, "�����ʻ�ȡ����", "����", JOptionPane.INFORMATION_MESSAGE);
					}
					else {
						//�������������ʾ���ȡ��ȷʱ
						int bps = Integer.parseInt(bpsStr);
						try {
							
							//��ȡָ���˿����������ʵĴ��ڶ���
							serialPort = SerialTool.openPort(commName, bps);
							//�ڸô��ڶ�������Ӽ�����
							SerialTool.addListener(serialPort, new SerialListener());
							//�����ɹ�������ʾ
							JOptionPane.showMessageDialog(null, "�����ɹ����Ժ���ʾ������ݣ�", "��ʾ", JOptionPane.INFORMATION_MESSAGE);
							
						} catch (SerialPortParameterFailure | NotASerialPort | NoSuchPort | PortInUse | TooManyListeners e1) {
							//��������ʱʹ��һ��Dialog��ʾ����Ĵ�����Ϣ
							JOptionPane.showMessageDialog(null, e1, "����", JOptionPane.INFORMATION_MESSAGE);
						}
					}
				}
				
			}
		});
		
		
		this.setResizable(false);
		
		new Thread(new RepaintThread()).start();	//�����ػ��߳�
		
	}
	
	/**
	 * �������������Ԫ��
	 */
	public void paint(Graphics g) {
		@SuppressWarnings("unused")
		Color c = g.getColor();
		
		g.setColor(Color.black);
		g.setFont(new Font("΢���ź�", Font.BOLD, 25));
		g.drawString(" �ܽ����� ", 45, 130);

		g.setColor(Color.black);
		g.setFont(new Font("΢���ź�", Font.BOLD, 25));
		g.drawString(" PH�� ", 425, 130);
		
		g.setColor(Color.black);
		g.setFont(new Font("΢���ź�", Font.BOLD, 25));
		g.drawString(" �Ƕȣ� ", 45, 220);
		
		g.setColor(Color.black);
		g.setFont(new Font("΢���ź�", Font.BOLD, 25));
		g.drawString(" �¶ȣ� ", 425, 220);
		
		
		g.setColor(Color.gray);
		g.setFont(new Font("΢���ź�", Font.BOLD, 20));
		g.drawString(" ����ѡ�� ", 45, 410);
		
		g.setColor(Color.gray);
		g.setFont(new Font("΢���ź�", Font.BOLD, 20));
		g.drawString(" �����ʣ� ", 425, 410);
		
	}
	
	/**
	 * ˫���巽ʽ�ػ������Ԫ�����
	 */
	public void update(Graphics g) {
		if (offScreen == null)	offScreen = this.createImage(Client.WIDTH, Client.HEIGHT);
		Graphics gOffScreen = offScreen.getGraphics();
		Color c = gOffScreen.getColor();
		gOffScreen.setColor(Color.white);
		gOffScreen.fillRect(0, 0, Client.WIDTH, Client.HEIGHT);	//�ػ���������
		this.paint(gOffScreen);	//�ػ�����Ԫ��
		gOffScreen.setColor(c);
		g.drawImage(offScreen, 0, 0, null);	//���»��õĻ�����������ԭ������
	}
	
	/*
	 * �ػ��̣߳�ÿ��30�����ػ�һ�Σ�
	 */
	private class RepaintThread implements Runnable {
		
		public void run() {
			
			
			while(true) {
				//�����ػ�����
				repaint();
				
				
				
				//ɨ����ô���
				commList = SerialTool.findPort();
				if (commList != null && commList.size()>0) {
					
					//�����ɨ�赽�Ŀ��ô���
					for (String s : commList) {
						
						//�ô������Ƿ��Ѵ��ڣ���ʼĬ��Ϊ�����ڣ���commList����ڵ���commChoice�ﲻ���ڣ�������ӣ�
						boolean commExist = false;	
						
						for (int i=0; i<commChoice.getItemCount(); i++) {
							if (s.equals(commChoice.getItem(i))) {
								//��ǰɨ�赽�Ĵ������Ѿ��ڳ�ʼɨ��ʱ����
								commExist = true;
								break;
							}					
						}
						
						if (commExist) {
							//��ǰɨ�赽�Ĵ������Ѿ��ڳ�ʼɨ��ʱ���ڣ�ֱ�ӽ�����һ��ѭ��
							continue;
						}
						else {
							//��������������´����������ô��������б�
							commChoice.add(s);
						}
					}
					
					//�Ƴ��Ѿ������õĴ���
					for (int i=0; i<commChoice.getItemCount(); i++) {
						
						//�ô����Ƿ���ʧЧ����ʼĬ��Ϊ�Ѿ�ʧЧ����commChoice����ڵ���commList�ﲻ���ڣ����Ѿ�ʧЧ��
						boolean commNotExist = true;	
						
						for (String s : commList) {
							if (s.equals(commChoice.getItem(i))) {
								commNotExist = false;	
								break;
							}
						}
						
						if (commNotExist) {
							//System.out.println("remove" + commChoice.getItem(i));
							commChoice.remove(i);
						}
						else {
							continue;
						}
					}
					
				}
				else {
					//���ɨ�赽��commListΪ�գ����Ƴ��������д���
					commChoice.removeAll();
				}

				try {
					Thread.sleep(30);
				} catch (InterruptedException e) {
					String err = ExceptionWriter.getErrorInfoFromException(e);
					JOptionPane.showMessageDialog(null, err, "����", JOptionPane.INFORMATION_MESSAGE);
					System.exit(0);
				}
			}
		}
		
	}
	
	/**
	 * ���ڲ�����ʽ����һ�����ڼ�����
	 * @author zhong
	 *
	 */
	private class SerialListener implements SerialPortEventListener {
		
		
		
	    public void serialEvent(SerialPortEvent serialPortEvent) {
	    	
	    	
	    
	        
	        
	    	
	        switch (serialPortEvent.getEventType()) {

	            case SerialPortEvent.BI: // 10 ͨѶ�ж�
	            	JOptionPane.showMessageDialog(null, "�봮���豸ͨѶ�ж�", "����", JOptionPane.INFORMATION_MESSAGE);
	            	break;

	            case SerialPortEvent.OE: // 7 ��λ�����������

	            case SerialPortEvent.FE: // 9 ֡����

	            case SerialPortEvent.PE: // 8 ��żУ�����

	            case SerialPortEvent.CD: // 6 �ز����

	            case SerialPortEvent.CTS: // 3 �������������

	            case SerialPortEvent.DSR: // 4 ����������׼������

	            case SerialPortEvent.RI: // 5 ����ָʾ

	            case SerialPortEvent.OUTPUT_BUFFER_EMPTY: // 2 ��������������
	            	break;
	            
	            case SerialPortEvent.DATA_AVAILABLE: // 1 ���ڴ��ڿ�������
	            	
	            	//System.out.println("found data");
					byte[] data = null;
					
					try {
						if (serialPort == null) {
							JOptionPane.showMessageDialog(null, "���ڶ���Ϊ�գ�����ʧ�ܣ�", "����", JOptionPane.INFORMATION_MESSAGE);
						}
						else {
							data = SerialTool.readFromPort(serialPort);	//��ȡ���ݣ������ֽ�����
							//System.out.println(new String(data));
							
							//�Զ����������
							if (data == null || data.length < 1) {	//��������Ƿ��ȡ��ȷ	
								JOptionPane.showMessageDialog(null, "��ȡ���ݹ�����δ��ȡ����Ч���ݣ������豸�����", "����", JOptionPane.INFORMATION_MESSAGE);
								System.exit(0);
							}
							else {
//								String returnDate = getHexStringOfInteger(data);
								String dataOriginal = new String(data);	//���ֽ���������ת��λΪ������ԭʼ���ݵ��ַ���
//								System.out.println(dataOriginal);
								
//							    String str = "rongjieyang&&PH&&zuodu&&We:NM-energy not upStart08.0 1.15 0.16% 30";
//							    String str1 = "rongjieyang&&PH&&zuodu&&We:NM-energy not upStart0";
//							    dataOriginal = dataOriginal.replaceAll(str1,"");
//								System.out.println(dataOriginal);

							    String reg = " +";//���ն���ո��������и�
							    String[] arr = dataOriginal.split(reg);
//							    System.out.println(arr.length);

							    String rongjiryang =arr[0];
							    String PH = arr[1];
							    String zuodu = arr[2];
							    String Wen = arr[3];
//							    for(String s : arr)
//							    {
//							        System.out.println(s);
//							    }
//
//						        System.out.println(rongjiryang);
//						        System.out.println(PH);
//						        System.out.println(zuodu);
//						        System.out.println(Wen);

							    
							    try {
							    	tem.setText(rongjiryang);
									hum.setText(PH);
									pa.setText(zuodu);
									rain.setText(Wen + " ��");
									
								} catch (ArrayIndexOutOfBoundsException e) {
									JOptionPane.showMessageDialog(null, "���ݽ������̳������½�������ʧ�ܣ������豸�����", "����", JOptionPane.INFORMATION_MESSAGE);
									System.exit(0);
								}
							   
								//������һ����ʱ��,ÿһ��Сʱִ��һ��,�ϴ�����
//								Timer time = new Timer();
//							    time.schedule(new MyTask(rongjiryang,PH,zuodu,Wen), 1000, 3600000);   //1s֮��ʼִ�У�ÿһСʱִ��һ�Σ�������λ�����룩
//								String dataValid = "";	//��Ч���ݣ���������ԭʼ�����ַ���ȥ���ͷ*���Ժ���ַ�����
//								String[] elements = null;	//�������水�ո���ԭʼ�ַ�����õ����ַ�������	
//								//��������
//								if (dataOriginal.charAt(0) == '*') {	//�����ݵĵ�һ���ַ���*��ʱ��ʾ���ݽ�����ɣ���ʼ����							
//									dataValid = dataOriginal.substring(1);
//									elements = dataValid.split(" ");
//									if (elements == null || elements.length < 1) {	//��������Ƿ������ȷ
//										JOptionPane.showMessageDialog(null, "���ݽ������̳��������豸�����", "����", JOptionPane.INFORMATION_MESSAGE);
//										System.exit(0);
//									}
//									else {
//										try {
//											//���½���Labelֵ
//											/*for (int i=0; i<elements.length; i++) {
//												System.out.println(elements[i]);
//											}*/
//											//System.out.println("win_dir: " + elements[5]);
//											tem.setText(elements[0] + " ��");
//											hum.setText(elements[1] + " %");
//											pa.setText(elements[2] + " hPa");
//											rain.setText(elements[3] + " mm");
//											
//										} catch (ArrayIndexOutOfBoundsException e) {
//											JOptionPane.showMessageDialog(null, "���ݽ������̳������½�������ʧ�ܣ������豸�����", "����", JOptionPane.INFORMATION_MESSAGE);
//											System.exit(0);
//										}
//									}	
//								}
							}
							
						}						
						
					} catch (ReadDataFromSerialPortFailure | SerialPortInputStreamCloseFailure e) {
						JOptionPane.showMessageDialog(null, e, "����", JOptionPane.INFORMATION_MESSAGE);
						System.exit(0);	//������ȡ����ʱ��ʾ������Ϣ���˳�ϵͳ
					}	
		            
					break;
	
	        }

	    }

	}
	
	
}
