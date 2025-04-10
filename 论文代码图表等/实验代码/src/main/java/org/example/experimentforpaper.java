package org.example;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class experimentforpaper {


    List<ArrayList<Integer>>data = new ArrayList<>();
    public void initdata(File file){
        try {
            FileReader fis = new FileReader(file);
            BufferedReader bis = new BufferedReader(fis);
//            char[]buffer = new char[1024];
//            int len;
            String line = null;
            List<String> strdata = new ArrayList<String>();
            while((line = bis.readLine()) != null){
                strdata.add(line);
//                System.out.println(line);
            }
            for(String a:strdata){
                int x = 0, tail = 0, flag  = 1;      //这里需要手动调整flag的值，寿司需要设置为0，其他的设置为1
                ArrayList<Integer> linedata = new ArrayList<>();
                for(int i=0; i<a.length(); i++){
                    if(a.charAt(i) == ','){
                        if(flag == 1)
                            linedata.add(x);
                        else
                            flag = 1;
                        x = 0;
                    }else{
                        x = x*10+a.charAt(i)-'0';
                    }
                }
                linedata.add(x);
                data.add(linedata);
//                System.out.println(linedata);
            }


        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    //求最终排序 与 原始数据的平均肯德尔距离!!!!!!!!!
    public double kendall(ArrayList<Integer> res){
        int n = data.size(), m = data.get(0).size();
        int[] a = new int[m];
        for(int i=0; i<res.size(); i++){
            int x = res.get(i);
            a[x-1] = i;   //需要减一，因为下标从0开始,选项从1开始的
        }
        long cot = 0;
        for(ArrayList<Integer> linedata: data){
//            int m = linedata.size();
            for(int i=0; i<m; i++){
                for(int j=i+1; j<m; j++){
                    int l =  linedata.get(i), r = linedata.get(j);
                    int l1 = a[l-1], r1 = a[r-1];   //选项对应的位次
                    if(l1 > r1 )
                        cot++;
                }
            }
        }
//        System.out.println("cot : "+cot+"  n:"+n+"  m:"+m);
        return (double)cot*2/((long)n*m*(m-1));
    }

    //求Spearman相关系数的算法！！！！！！！！！！
    public double spearman(ArrayList<Integer>res){
        int n = data.size(), m = data.get(0).size();
        long cot = 0;
        int[] a = new int[m];
        for(int i=0; i<res.size(); i++){
            int x = res.get(i);
            a[x-1] = i;
        }
        for(ArrayList<Integer> linedata : data){
            for(int i=0; i<m; i++){
                int x = linedata.get(i);
                cot += Math.abs(i-a[x-1]);
            }
        }
        if(m%2 == 1)    //归一化处理，最大的偏差就是逆序排序，根据选项的奇偶性不同！！！！！！
            return (double)cot*2/((long)n*(m+1)*(m-1));
        else
            return (double)cot*2/((long)n*m*m);
    }


    /**
     * 求收集的分数的误差
     * @param res
     * @return  坐归一化处理
     */
    public double scoreError(ArrayList<Double> res){  //传入一个百分之的打分表即最高的哪个数是100( 无序！！！！)
        int m = data.get(0).size(), n  = data.size();
        double[]totalscore = new double[m];
        for(ArrayList<Integer> linedata : data){
            for(int i=0; i<linedata.size(); i++){
                int x = m-i; //分数
                totalscore[linedata.get(i)-1] += x;
            }

        }
//        System.out.println(res);
        double v = 0, b = 0;
        for(int i=0; i<m; i++){
            totalscore[i] /= n;
//            v += totalscore[i];
//            b += res.get(i);
//            System.out.print(totalscore[i]+", ");
        }
//        System.out.println();
//        System.out.println(v+"   "+b+"   "+(v/m)+"   "+(b/m));
        double ans = 0;
        for(int i=0; i<m; i++){   //平均误差率
           ans += Math.abs( res.get(i)-totalscore[i])/totalscore[i];
//           ans += Math.abs(res.get(i)-totalscore[i]);
        }
//        for(int i=0; i<m; i++)
//        System.out.print(totalscore[i]+", ");
//        System.out.println();
        return ans/m;
    }


    //转化为百分制
    public void toCentesimalSystem(double[] scores){
        int m = scores.length;
        double maxval = 0;
        for(int i=0; i<m; i++)
            maxval = Math.max(scores[i], maxval);
        for(int i=0; i<m; i++){
            scores[i] = 100*scores[i]/maxval;
        }
    }



    public static void main(String[] args) {
//        1 : 创建对象并初始化数据集（将数据从读取）
        experimentforpaper experimentforpaper = new experimentforpaper();
        File file = new File("C:\\Users\\12265\\Desktop\\mypapper\\experimentdata\\Mallows_100_15_20000.txt");
        experimentforpaper.initdata(file);   //初始化数据，读取txt文件中的数据并存储
        int m = experimentforpaper.data.get(0).size();
        int n = experimentforpaper.data.size();


        //全局参数设置
        int times = 1000;
        double eclipse = 1;
        int K = 1;





        System.out.println("***************************************************************************");

        double kendalldis = 0;
        double spearmandis = 0;
        double scoreerror = 0;
        double[] averagesroce = new double[m];
        //重复   10   次，计算平局的肯德尔距离 和 斯皮尔曼距离
        for(int z = 0; z<times; z++) {
            MarkSort markSort = new MarkSort();
            ArrayList<int[]> R = markSort.markSortpart1(experimentforpaper.data, K, eclipse, m - 1);
            ArrayList<double[]> X = markSort.markSortpart2(experimentforpaper.data, R, eclipse );
//            System.out.println(X);
            ArrayList<Integer> res = experimentforpaper.getrank(X);
            ArrayList<Double> score = experimentforpaper.getsorce(X, 8);  //最高分100分
            for(int i=0; i<m; i++)
                averagesroce[i] += score.get(i);
//            System.out.println(socre);
//        测试的代码顺序排列与数据集的肯德尔和斯皮尔距离分别为  ：  0.43721297424098887       0.6005927730410069
            double kendall = experimentforpaper.kendall(res);
            double spearman = experimentforpaper.spearman(res);
            double v = experimentforpaper.scoreError(score);

//            System.out.println(kendall + "       " + spearman);
            kendalldis += kendall;
            spearmandis += spearman;
            scoreerror += v;
        }
        for(int i=0; i<m; i++)
            averagesroce[i] /= times;
        experimentforpaper.toCentesimalSystem(averagesroce);
        for(int i=0; i<m; i++)
            System.out.print(String.format("%.2f ", averagesroce[i])+",  ");
        System.out.println();
        System.out.println("ending : "+(kendalldis/times)+"   "+(spearmandis/times) +"   "+scoreerror/times);


        System.out.println("***************************************************************************");

//        kendalldis = 0;
//        spearmandis = 0;
//        for(int z = 0; z<times; z++) {
//            ImpMarkSort impMarkSort = new ImpMarkSort();
//            ArrayList<Object[]> R = impMarkSort.markSortpart1(experimentforpaper.data, K, eclipse, m - 1);
//            ArrayList<double[]> X = impMarkSort.markSortpart2(experimentforpaper.data, R, eclipse );
////            System.out.println(X);
//            ArrayList<Integer> res = experimentforpaper.getrank(X);
//            ArrayList<Double> score = experimentforpaper.getsorce(X, 8);  //最高分100分
//            for(int i=0; i<m; i++)
//                averagesroce[i] += score.get(i);
////            System.out.println(socre);
////        测试的代码顺序排列与数据集的肯德尔和斯皮尔距离分别为  ：  0.43721297424098887       0.6005927730410069
//            double kendall = experimentforpaper.kendall(res);
//            double spearman = experimentforpaper.spearman(res);
//            double v = experimentforpaper.scoreError(score);
//
////            System.out.println(kendall + "       " + spearman);
//            kendalldis += kendall;
//            spearmandis += spearman;
//            scoreerror += v;
//        }
//        for(int i=0; i<m; i++)
//            averagesroce[i] /= times;
//        experimentforpaper.toCentesimalSystem(averagesroce);
//        for(int i=0; i<m; i++)
//            System.out.print(String.format("%.2f ", averagesroce[i])+",  ");
//        System.out.println();
//        System.out.println("ending : "+(kendalldis/times)+"   "+(spearmandis/times) +"   "+scoreerror/times);


        System.out.println("***************************************************************************");

//        kendalldis = 0;
//        spearmandis = 0;
//        //HRA测试！！！！！！！！
//        for(int i=0; i<100; i++){
//            HRA hra = new HRA();
//            ArrayList<int[]> ints = hra.kwitSortpart1(experimentforpaper.data, 1, 1, 0.01);
//            ArrayList<Integer> res = hra.kwitSortpart2(experimentforpaper.data, ints);
//            double kendall = experimentforpaper.kendall(res);
//            double spearman = experimentforpaper.spearman(res);
////            System.out.println(kendall+"   "+spearman);
//            kendalldis += kendall;
//            spearmandis += spearman;
//        }
//        System.out.println("ending : "+(kendalldis/100)+"   "+(spearmandis)/100 );
        kendalldis = 0;
        spearmandis = 0;
        //HRA测试！！！！！！！！
        for(int i=0; i<times; i++){
            HelnakSort helnakSort = new HelnakSort();
            ArrayList<int[]> ints = helnakSort.HelnakSortPart1(experimentforpaper.data, K, eclipse);
            ArrayList<Integer> res = helnakSort.HelnakSortPart2(experimentforpaper.data, ints);
            double kendall = experimentforpaper.kendall(res);
            double spearman = experimentforpaper.spearman(res);
//            System.out.println(kendall+"   "+spearman);
            kendalldis += kendall;
            spearmandis += spearman;
        }
        System.out.println("ending : "+(kendalldis/times)+"   "+(spearmandis)/times );


        System.out.println("***************************************************************************");

        kendalldis = 0;
        spearmandis = 0;
        scoreerror = 0;
        //KwitSort测试！！！！！！！！
        for(int i=0; i<times; i++){
            KwitSort kwitSort = new KwitSort();
            ArrayList<int[]> ints = kwitSort.kwitSortpart1(experimentforpaper.data, K, eclipse);
            ArrayList<double[]> X = kwitSort.kwitSortpart2(experimentforpaper.data, ints, K, eclipse);
            ArrayList<Integer> res = experimentforpaper.getrank(X);
//            ArrayList<Double> sorce = experimentforpaper.getsorce(X, 100);
            double kendall = experimentforpaper.kendall(res);
            double spearman = experimentforpaper.spearman(res);
//            double v = experimentforpaper.scoreError(sorce, 100);
//            System.out.println(kendall+"   "+spearman);
            kendalldis += kendall;
            spearmandis += spearman;
//            scoreerror += v;
        }
        System.out.println("ending : "+(kendalldis/times)+"   "+(spearmandis/times) );

    }


    //得到排名数据
    public  ArrayList<Integer> getrank(ArrayList<double[]>X){
        ArrayList<Integer>res = new ArrayList<>();
        for (int i = 0; i < X.size(); i++)
            res.add((int) (X.get(i)[0]));
        return res;
    }

    //得到归一化的打分数据, 最高分是设定的分数
    public ArrayList<Double> getsorce(ArrayList<double[]>X, double mean){
        int m = X.size();
//        double max = X.get(0)[1], min = X.get(m-1)[1];
//        if(min < 0)max-=min;
        Collections.sort(X, ((o1, o2) -> o1[0]>o2[0]? 1: -1));
        ArrayList<Double>score = new ArrayList<>();
        for(int i=0; i<X.size(); i++) {
            double x = X.get(i)[1];
//            if(min < 0)
//                x -= min;
            score.add(x+mean);
        }
//        System.out.println(score);
        return score;
    }



}
