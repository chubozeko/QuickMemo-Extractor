import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.io.EOFException;
import java.io.File;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Extractor extends JFrame{
	
	public static void main(String[] args) {
		
		String file_str = "";
		
		try{
			// OPEN FILE TO READ
			JFileChooser jfc = new JFileChooser("c:\\");
			jfc.setDialogTitle("Select File(s) to Extract");
			jfc.setSelectedFile(null);
			jfc.setMultiSelectionEnabled(true);
			jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
			jfc.setFileFilter(new FileNameExtensionFilter("QuickMemo+ Files", "jlqm"));
			//jfc.addChoosableFileFilter(new FileNameExtensionFilter("QuickMemo+ Files", "jlqm"));
			int option = jfc.showOpenDialog(null);
			if (option != JFileChooser.APPROVE_OPTION)
				return;
			File[] files = jfc.getSelectedFiles();
			String fileDir = files[0].getParent();
			
			for(int i=0; i<files.length; i++) {
				// READ SELECTED FILE
				file_str = "";
				DataInputStream dis = new DataInputStream(new FileInputStream(files[i]));
				int byte1, fileNo = i+1;
				do{
					byte1 = dis.read();
					file_str += (char) byte1;
				} while (byte1 != -1);
				JSONObject obj = new JSONObject( file_str );
				//String accName = obj.getJSONObject("Category").getString("AccountName");
				String description = obj.getJSONArray("MemoObjectList").getJSONObject(0).getString("DescRaw");
				
				// SAVE CONTENT TO NEW FILE
				String newFileName = "quickmemo_" + fileNo; // Specify a default file name.
				newFileName += ".txt";
				jfc = new JFileChooser(fileDir);
				File saveFile = new File(newFileName);
				jfc.setSelectedFile(saveFile); 
				jfc.setDialogTitle("Save File(s) To...");
				
				jfc.setFileFilter(new FileNameExtensionFilter("Text File", "txt", "text"));
				//jfc.addChoosableFileFilter(new FileNameExtensionFilter("Text File", "txt", "text"));
				option = jfc.showSaveDialog(null);
				if (option != JFileChooser.APPROVE_OPTION)
					return; // User canceled or clicked the dialog�s close box.
				saveFile = jfc.getSelectedFile();
				if (saveFile.exists()) { // Ask the user whether to replace the file.
					int response = JOptionPane.showConfirmDialog( null,
							"The file \"" + saveFile.getName()
							+ "\" already exists.\nDo you want to replace it?",
							"Confirm Save",
							JOptionPane.YES_NO_OPTION,
							JOptionPane.WARNING_MESSAGE );
					if (response != JOptionPane.YES_OPTION)
						return; // User does not want to replace the file.
				}
				
				// WRITE TO NEW FILE
				DataOutputStream dos = new DataOutputStream(new FileOutputStream(saveFile));
				//dos.writeBytes("Account: " + accName + "\r\n");
				dos.writeBytes( description );
				dos.flush();
				dos.close();
			}
			
			JOptionPane.showMessageDialog(null, "Files have been extracted to: \n" + fileDir);
			
			/* TEST OUTPUT LOGS */
//			System.out.println( "Account Name is " + accName );
//			System.out.println( description );
//			System.out.println( file_str );
			
		} catch (Exception ex){
			System.out.println( ex );
		}
	}

}
