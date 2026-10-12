package com.example.prawnhub_v2;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/** Creates shareable reports from actual Firebase sensor history. */
public class ReportsActivity extends AppCompatActivity {
    private final Calendar start = Calendar.getInstance(), end = Calendar.getInstance();
    private final SimpleDateFormat date = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
    private final SimpleDateFormat timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US);
    private EditText startInput, endInput; private TextView preview;

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        if (!"admin".equalsIgnoreCase(SessionStore.getRole(this))) { finish(); return; }
        setContentView(R.layout.activity_reports);
        startInput=findViewById(R.id.startDateInput); endInput=findViewById(R.id.endDateInput); preview=findViewById(R.id.reportPreviewText);
        findViewById(R.id.backButton).setOnClickListener(v->finish()); start.add(Calendar.DAY_OF_MONTH,-7);
        startInput.setOnClickListener(v->pick(start,startInput)); endInput.setOnClickListener(v->pick(end,endInput));
        ((Button)findViewById(R.id.exportPdfButton)).setOnClickListener(v->export(true)); ((Button)findViewById(R.id.exportExcelButton)).setOnClickListener(v->export(false));
        refreshInputs(); load(rows->preview.setText(summary(rows)));
    }
    private void pick(Calendar selected,EditText field) { new DatePickerDialog(this,(v,y,m,d)->{ selected.set(y,m,d); field.setText(date.format(selected.getTime())); load(rows->preview.setText(summary(rows))); },selected.get(Calendar.YEAR),selected.get(Calendar.MONTH),selected.get(Calendar.DAY_OF_MONTH)).show(); }
    private void refreshInputs(){startInput.setText(date.format(start.getTime()));endInput.setText(date.format(end.getTime()));}
    private boolean valid(){if(start.after(end)){preview.setText("Start date must not be after end date.");return false;}return true;}
    private long boundary(Calendar source,boolean upper){Calendar c=(Calendar)source.clone();c.set(Calendar.HOUR_OF_DAY,upper?23:0);c.set(Calendar.MINUTE,upper?59:0);c.set(Calendar.SECOND,upper?59:0);c.set(Calendar.MILLISECOND,upper?999:0);return c.getTimeInMillis();}
    private void load(Callback callback){if(!valid())return;preview.setText("Loading records...");FirebaseDatabase.getInstance().getReference("ShrimpHub/history").addListenerForSingleValueEvent(new ValueEventListener(){@Override public void onDataChange(@NonNull DataSnapshot root){List<Row> rows=new ArrayList<>();for(DataSnapshot child:root.getChildren()){long time=parse(child.child("timestamp").getValue());if(time<boundary(start,false)||time>boundary(end,true))continue;Float temp=n(child,"temperature"),tds=n(child,"tds"),turb=n(child,"turbidity"),water=n(child,"waterLevel");if(temp!=null||tds!=null||turb!=null||water!=null)rows.add(new Row(time,temp,tds,turb,water));}callback.done(rows);}@Override public void onCancelled(@NonNull DatabaseError e){preview.setText("Could not load records: "+e.getMessage());}});}
    private Float n(DataSnapshot s,String key){Object v=s.child(key).getValue();if(v instanceof Number)return ((Number)v).floatValue();try{return v==null?null:Float.parseFloat(String.valueOf(v));}catch(NumberFormatException e){return null;}}
    private long parse(Object value){if(value instanceof Number){long raw=((Number)value).longValue();return raw<100000000000L?raw*1000:raw;}if(value==null)return -1;String text=String.valueOf(value);try{return Long.parseLong(text);}catch(NumberFormatException ignored){}for(String pattern:new String[]{"yyyy-MM-dd HH:mm:ss","yyyy-MM-dd HH:mm","yyyy-MM-dd'T'HH:mm:ss"})try{Date parsed=new SimpleDateFormat(pattern,Locale.US).parse(text);if(parsed!=null)return parsed.getTime();}catch(Exception ignored){}return -1;}
    private String summary(List<Row> rows){return rows.isEmpty()?"No valid sensor records in the selected date range.":rows.size()+" sensor records ready for export.";}
    private void export(boolean pdf){if(!valid())return;load(rows->{preview.setText(summary(rows));if(rows.isEmpty()){Toast.makeText(this,"Nothing to export.",Toast.LENGTH_LONG).show();return;}try{File file=pdf?pdf(rows):xlsx(rows);Uri uri=FileProvider.getUriForFile(this,getPackageName()+".fileprovider",file);Intent share=new Intent(Intent.ACTION_SEND).setType(pdf?"application/pdf":"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet").putExtra(Intent.EXTRA_STREAM,uri).addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);startActivity(Intent.createChooser(share,"Share PrawnHub report"));}catch(IOException e){Toast.makeText(this,"Export failed: "+e.getMessage(),Toast.LENGTH_LONG).show();}});}
    private File file(String suffix){File directory=getExternalFilesDir("reports");if(directory!=null&&!directory.exists())directory.mkdirs();return new File(directory,"prawnhub-report-"+System.currentTimeMillis()+suffix);}
    private File pdf(List<Row> rows)throws IOException{File output=file(".pdf");PdfDocument document=new PdfDocument();PdfDocument.Page page=null;android.graphics.Canvas canvas=null;Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG);int line=0,pageNo=1;for(Row row:rows){if(canvas==null||line>45){if(page!=null)document.finishPage(page);page=document.startPage(new PdfDocument.PageInfo.Builder(595,842,pageNo++).create());canvas=page.getCanvas();paint.setTextSize(15);canvas.drawText("PrawnHub Farm Report",40,42,paint);paint.setTextSize(9);canvas.drawText("Range: "+date.format(start.getTime())+" - "+date.format(end.getTime()),40,60,paint);canvas.drawText("Timestamp | Temperature C | TDS ppm | Turbidity NTU | Water Level cm",40,80,paint);line=0;}canvas.drawText(row.display(),40,100+(line++*15),paint);}if(page!=null)document.finishPage(page);document.writeTo(new FileOutputStream(output));document.close();return output;}
    private File xlsx(List<Row> rows)throws IOException{File output=file(".xlsx");try(ZipOutputStream zip=new ZipOutputStream(new FileOutputStream(output))){put(zip,"[Content_Types].xml","<?xml version=\"1.0\"?><Types xmlns=\"http://schemas.openxmlformats.org/package/2006/content-types\"><Default Extension=\"rels\" ContentType=\"application/vnd.openxmlformats-package.relationships+xml\"/><Default Extension=\"xml\" ContentType=\"application/xml\"/><Override PartName=\"/xl/workbook.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet.main+xml\"/><Override PartName=\"/xl/worksheets/sheet1.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.worksheet+xml\"/></Types>");put(zip,"_rels/.rels","<?xml version=\"1.0\"?><Relationships xmlns=\"http://schemas.openxmlformats.org/package/2006/relationships\"><Relationship Id=\"rId1\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument\" Target=\"xl/workbook.xml\"/></Relationships>");put(zip,"xl/workbook.xml","<?xml version=\"1.0\"?><workbook xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\" xmlns:r=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships\"><sheets><sheet name=\"Sensor Records\" sheetId=\"1\" r:id=\"rId1\"/></sheets></workbook>");put(zip,"xl/_rels/workbook.xml.rels","<?xml version=\"1.0\"?><Relationships xmlns=\"http://schemas.openxmlformats.org/package/2006/relationships\"><Relationship Id=\"rId1\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/worksheet\" Target=\"worksheets/sheet1.xml\"/></Relationships>");StringBuilder sheet=new StringBuilder("<?xml version=\"1.0\"?><worksheet xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\"><sheetData>");sheet.append(xmlRow(1,new String[]{"Timestamp","Temperature (C)","TDS (ppm)","Turbidity (NTU)","Water Level (cm)"}));int i=2;for(Row row:rows)sheet.append(xmlRow(i++,row.cells()));sheet.append("</sheetData></worksheet>");put(zip,"xl/worksheets/sheet1.xml",sheet.toString());}return output;}
    private void put(ZipOutputStream zip,String path,String content)throws IOException{zip.putNextEntry(new ZipEntry(path));zip.write(content.getBytes(java.nio.charset.StandardCharsets.UTF_8));zip.closeEntry();}
    private String xmlRow(int n,String[] cells){StringBuilder text=new StringBuilder("<row r=\"").append(n).append("\">");for(String cell:cells)text.append("<c t=\"inlineStr\"><is><t>").append(cell.replace("&","&amp;").replace("<","&lt;")).append("</t></is></c>");return text.append("</row>").toString();}
    private interface Callback{void done(List<Row> rows);} private final class Row{final long time;final Float temp,tds,turb,water;Row(long time,Float temp,Float tds,Float turb,Float water){this.time=time;this.temp=temp;this.tds=tds;this.turb=turb;this.water=water;}String f(Float value){return value==null?"":String.format(Locale.US,"%.2f",value);}String[] cells(){return new String[]{timestamp.format(new Date(time)),f(temp),f(tds),f(turb),f(water)};}String display(){return String.join(" | ",cells());}}
}
