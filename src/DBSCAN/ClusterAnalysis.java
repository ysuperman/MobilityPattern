package DBSCAN;

import java.util.ArrayList;
import java.util.List;

public class ClusterAnalysis {
    public List<Cluster> doDbscanAnalysis(List<DataPoint> dataPoints,
            double radius, int ObjectNum) {
         List<Cluster> clusterList=new ArrayList<Cluster>();
         for(int i=0; i<dataPoints.size();i++){
             DataPoint dp=dataPoints.get(i);
             List<DataPoint> arrivableObjects=isKeyAndReturnObjects(dp,dataPoints,radius,ObjectNum);
             if(arrivableObjects!=null){
                  Cluster tempCluster=new Cluster();
                  tempCluster.setClusterName("Cluster "+i);
                  tempCluster.setDataPoints(arrivableObjects);
                  clusterList.add(tempCluster);
             }
         }

         for(int i=0;i<clusterList.size();i++){
             for(int j=0;j<clusterList.size();j++){
                  if(i!=j){
                      Cluster clusterA=clusterList.get(i);
                      Cluster clusterB=clusterList.get(j);

                      List<DataPoint> dpsA=clusterA.getDataPoints();
                      List<DataPoint> dpsB=clusterB.getDataPoints();

                      boolean flag=mergeList(dpsA,dpsB);
                      if(flag){
                          clusterList.set(j, new Cluster());
                      }
                  }
             }
         }

         return clusterList;
    }

   

    public void displayCluster(List<Cluster> clusterList){
        if(clusterList!=null){
            for(Cluster tempCluster:clusterList){
                if(tempCluster.getDataPoints()!=null&&tempCluster.getDataPoints().size()>0){
                    System.out.println("----------"+tempCluster.getClusterName()+"----------");
                    for(DataPoint dp:tempCluster.getDataPoints()){
                        System.out.println(dp.getDataPointName());
                    }
                }
            }
        }
    }

   
    private double getDistance(DataPoint dp1,DataPoint dp2){
        double distance=0.0;
        //double[] dim1=dp1.getDimensioin();
        //double[] dim2=dp2.getDimensioin();
        double lon1 = dp1.getLon();
        double lat1 = dp1.getLat();
        double lon2 = dp2.getLon();
        double lat2 = dp2.getLat();
        distance = distanceInGlobal(lon1,lat1,lon2,lat2);
        return distance;
    }
    /*
	 * 计算两位置之间的距离，根据球面坐标长度公式计算(单位：米)
	 * 注意，这个计算很耗时间,另外,这个计算把经纬度的100万倍还原了!
	 */
    public static double distanceInGlobal(double lon1, double lat1, double lon2, double lat2){
		double x1 = lon1;
		double y1 = lat1;
		double x2 = lon2;
		double y2 = lat2;

		double L = (3.1415926*6370/180)*Math.sqrt((Math.abs((x1)-(x2)))*(Math.abs((x1)-(x2)))*(Math.sin((90-(y1))*(3.1415926/180)))*(Math.sin((90-(y1))*(3.1415926/180)))+(Math.abs((y1)-(y2)))*(Math.abs((y1)-(y2))));
		return L * 1000;
	}
   
   private List<DataPoint> isKeyAndReturnObjects(DataPoint dataPoint,List<DataPoint> dataPoints,double radius,int ObjectNum){
       List<DataPoint> arrivableObjects=new ArrayList<DataPoint>(); //用来存储所有直接密度可达对象

       for(DataPoint dp:dataPoints){
          double distance=getDistance(dataPoint,dp);
          if(distance<=radius){
              arrivableObjects.add(dp);
          }
       }

       if(arrivableObjects.size()>=ObjectNum){
           dataPoint.setKey(true);
           return arrivableObjects;
       }

       return null;
   }

  
   private boolean isContain(DataPoint dp,List<DataPoint> dps){
      boolean flag=false;
      String name=dp.getDataPointName().trim();
      for(DataPoint tempDp:dps){
         String tempName=tempDp.getDataPointName().trim();
         if(name.equals(tempName)){
             flag=true;
             break;
         }
      }

      return flag;
   }

  
   private boolean mergeList(List<DataPoint> dps1,List<DataPoint> dps2){
       boolean flag=false;

       if(dps1==null||dps2==null||dps1.size()==0||dps2.size()==0){
           return flag;
       }

       for(DataPoint dp:dps2){
          if(dp.isKey()&&isContain(dp,dps1)){
             flag=true;
             break;
          }
       }

       if(flag){
           for(DataPoint dp:dps2){
              if(!isContain(dp,dps1)){
                  DataPoint tempDp=new DataPoint(dp);
                  dps1.add(tempDp);
              }
           }
       }


       return flag;
   }

   public static void main(String[] args){
       ArrayList<DataPoint> dpoints = new ArrayList<DataPoint>();
      /*
       double[] a={2,3};
       double[] b={2,4};
       double[] c={1,4};
       double[] d={1,3};
       double[] e={2,2};
       double[] f={3,2};

       double[] g={8,7};
       double[] h={8,6};
       double[] i={7,7};
       double[] j={7,6};
       double[] k={8,5};

       double[] l={100,2};//孤立点


       double[] m={8,20};
       double[] n={8,19};
       double[] o={7,18};
       double[] p={7,17};
       double[] q={8,21};

       dpoints.add(new DataPoint(a,"a",false));
       dpoints.add(new DataPoint(b,"b",false));
       dpoints.add(new DataPoint(c,"c",false));
       dpoints.add(new DataPoint(d,"d",false));
       dpoints.add(new DataPoint(e,"e",false));
       dpoints.add(new DataPoint(f,"f",false));

       dpoints.add(new DataPoint(g,"g",false));
       dpoints.add(new DataPoint(h,"h",false));
       dpoints.add(new DataPoint(i,"i",false));
       dpoints.add(new DataPoint(j,"j",false));
       dpoints.add(new DataPoint(k,"k",false));

       dpoints.add(new DataPoint(l,"l",false));

       dpoints.add(new DataPoint(m,"m",false));
       dpoints.add(new DataPoint(n,"n",false));
       dpoints.add(new DataPoint(o,"o",false));
       dpoints.add(new DataPoint(p,"p",false));
       dpoints.add(new DataPoint(q,"q",false));
		*/
       ClusterAnalysis ca=new ClusterAnalysis();
       List<Cluster> clusterList=ca.doDbscanAnalysis(dpoints, 2, 4);
       ca.displayCluster(clusterList);

   }
}