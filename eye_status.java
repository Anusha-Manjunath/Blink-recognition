import java.io.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
class eye_status
{
	public static void main(String args[]) throws IOException
	{
	int p=0x000000ff,q=0x0000ff00,s=0x00ff0000,i,j;
	File f=new File("sample_input.jpg");
	BufferedImage bi=ImageIO.read(f);
	System.out.println(bi);
	int w=bi.getWidth();
	int h=bi.getHeight();
	int rgb[][]=new int[w][h];	
	int r[][]=new int[w][h];
	int g[][]=new int[w][h];
	int b[][]=new int[w][h];
	int cr[][]=new int[w][h];
	int y[][]=new int[w][h];
	int a1[][]=new int[w][h];
	int di1[][]=new int[w][h];
	int di2[][]=new int[w][h];
	int di3[][]=new int[w][h];
	int di4[][]=new int[w][h];
	int di5[][]=new int[w][h];
	int di6[][]=new int[w][h];

// Color Conversion
	for(i=0;i<w;i++)
	{
		for(j=0;j<h;j++)
			{
				rgb[i][j]=bi.getRGB(i,j);
	
				r[i][j]=rgb[i][j] & s;
				r[i][j]=r[i][j]>>16;
				g[i][j]=rgb[i][j] & q;
				g[i][j]=g[i][j]>>8;
				b[i][j]=rgb[i][j] & p;
	
	
				cr[i][j]=(int)(((0.5*r[i][j])-(0.419*g[i][j])-(0.081*b[i][j]))+128);
				y[i][j]=(int)((0.299*r[i][j])+(0.587*g[i][j])+(0.114*b[i][j]));
				
				y[i][j]=y[i][j]<<8|y[i][j];
				y[i][j]=y[i][j]<<8|y[i][j];
		
				bi.setRGB(i,j,cr[i][j]) ;
			}
	}
	
	BufferedImage ycbcr=new BufferedImage(w,h,BufferedImage.TYPE_INT_RGB);
	
	for(i=0;i<w;i++)
	{
		for(j=0;j<h;j++)
			{
				ycbcr.setRGB(i,j,y[i][j]);
			}
	}	
	
	File f6=new File("y-img.jpg");
	ImageIO.write(ycbcr,"jpg",f6);

// Segmentation	 
	for(i=0;i<w;i++)
	{
		for(j=0;j<h;j++)
			{
				if(cr[i][j]>128 & cr[i][j]<165) cr[i][j]=16777215;
				else cr[i][j]=0;
				bi.setRGB(i,j,cr[i][j]);
			}
	}

	File f2=new File("seg-img.jpg");
	ImageIO.write(bi,"jpg",f2);
	
// Erosion	
	boolean flag=false;
	int n=5;  
	int mid=n/2;
	int count1=0;
	int count2=0;
	
	for(i=mid;i<w-mid;i++)                 
	{
		for(j=mid;j<h-mid;j++)
		{	
			label1: for(int x=i-mid;x<=i+mid;x++)   
						for(int y1=j-mid;y1<=j+mid;y1++)
					{
						if(cr[x][y1]==0)
						{  
							flag=true;
							break label1;
						}
						
						else
						{
							flag=false;
						}
					}
			if((cr[i][j]==16777215)&(flag==true))
			{
	
				a1[i][j]=0;
			}
			else
			{ 
				a1[i][j]=cr[i][j];
			}
			  di1[i][j]=a1[i][j];
						
		}
	}

// Dilation
	int m=30;     
	int mid1=m/2;
	
	for(i=mid1;i<w-mid1;i++)
	{
		for(j=mid1;j<h-mid1;j++)
		{	di1[i][j]=a1[i][j];
			label1: for(int x=i-mid1;x<=i+mid1;x++)
				for(int y1=j-mid1;y1<=j+mid1;y1++)
					{
						if(di1[x][y1]==16777215)
						{  
							flag=true;
							break label1;
						}
						
						else
						{
							flag=false;
						}
					}
	
	
			if((di1[i][j]==0)&(flag==true))
			{
				di2[i][j]=16777215;
			}
			else
			{ 
				di2[i][j]=di1[i][j];
			}
			  di3[i][j]=di2[i][j];			
		}
	}
	
	BufferedImage ero=new BufferedImage(w,h,BufferedImage.TYPE_INT_RGB);
	for(i=0;i<w;i++)
	{
		for(j=0;j<h;j++)
			{
				ero.setRGB(i,j,a1[i][j]);
			}
	}		
	File f3=new File("ero-img.jpg");
	ImageIO.write(ero,"jpg",f3);

	BufferedImage dil=new BufferedImage(w,h,BufferedImage.TYPE_INT_RGB);
	for(i=0;i<w;i++)
	{
		for(j=0;j<h;j++)
			{
				dil.setRGB(i,j,di2[i][j]);
			}
	}	
	File f4=new File("dil-img.jpg");
	ImageIO.write(dil,"jpg",f4);

// Masking
	for(i=0;i<w;i++)
	{
		for(j=0;j<h;j++)
		{
			di4[i][j]=y[i][j]&di2[i][j];
		}
	}
	
	
	BufferedImage mul=new BufferedImage(w,h,BufferedImage.TYPE_INT_RGB);
	for(i=0;i<w;i++)
	{
		for(j=0;j<h;j++)
			{
				mul.setRGB(i,j,di4[i][j]);
			}
	}		
	File f7=new File("mask-img.jpg");
	ImageIO.write(mul,"jpg",f7);
	
// Segmentation (eye)
	for(i=0;i<w;i++)
	{
		for(j=0;j<h;j++)
			{
				if((di4[i][j]>=131586)&(di4[i][j]<=1973790))
					di5[i][j]=16777215;
				else di5[i][j]=0;
			}
	}
	
	
	 BufferedImage eye=new BufferedImage(w,h,BufferedImage.TYPE_INT_RGB);
	for(i=0;i<w;i++)
	{
		for(j=0;j<h;j++)
			{
				eye.setRGB(i,j,di5[i][j]);
			}
	}		
	File f8=new File("eye-img.jpg");
	ImageIO.write(eye,"jpg",f8); 
	
	for(i=0;i<w;i++)
	{
		for(j=0;j<h;j++)
			{
				di6[i][j]=0;
			}
	}

// Eye Status
	for(i=190;i<=330;i++)
	    	for(j=93;j<=226;j++)
			{
				di6[i][j]=di5[i][j];
	    			if((di5[i][j]&0x0000ff)==255) 
	    				{
	    					count1++;		
	    				}
				else 
					{			
					}
	    		}
    					    			    			
	    for(i=330;i<480;i++)
	 	for(j=93;j<=226;j++)
	 		{ 
	    			di6[i][j]=di5[i][j];
	    			if((di5[i][j]&0x0000ff)==255) 
						{	  
							count2++;     
	    				}
				else
					{		
					}
	    		}
	    				
	    //System.out.println("count1="+count1);
		//System.out.println("count2="+count2);
	    			
	BufferedImage frame=new BufferedImage(w,h,BufferedImage.TYPE_INT_RGB);
	for(i=0;i<w;i++)
	{
		for(j=0;j<h;j++)
			{
				frame.setRGB(i,j,di6[i][j]);
			}
	}	
	File f9=new File("eye-frame-img.jpg");
	ImageIO.write(frame,"jpg",f9); 
	
	if(count1>500 && count2>500)
		 System.out.println("Eye status = Open. Both the eyes are open");
	else if(count1<500 && count2<500)
		 System.out.println("Eye status = Close. Both the eyes are closed");
	}
}