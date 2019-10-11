import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
//�ô�����   ��Ϣ����    ����ó��Ľ��  ������ѭ��������֤���Ĺ���673-683��
public class calculate {

	// �����б�
	public static List<String> featureList = new ArrayList<String>();
	// ����ֵ�б�
	public static List<List<String>> featureValueTableList = new ArrayList<List<String>>();
	// �õ�ȫ������
	public static Map<Integer, List<String>> tableMap = new HashMap<Integer, List<String>>();
    //��ȱʧֵ��ȫ������
	public static Map<Integer, List<String>> tableMap1 = new HashMap<Integer, List<String>>();
	
	
	/**
	 * ����������ݵõ���������Ԥ����
	 * 
	 * @param decisionTree
	 *            ������
	 * @param featureList
	 *            �����б�
	 * @param testDataList
	 *            ��������
	 * @return
	 */
	private static String getDTAnswer(Node decisionTree,
			List<String> featureList, List<String> testDataList) {
		if (featureList.size() - 1 != testDataList.size()) {
			System.out.println("�������ݲ�����");
			return "�������ݲ�����";
		}
		while (decisionTree != null) {
			// ������ӽڵ�Ϊ��,�򷵻ش˽ڵ��.
			if (decisionTree.childrenNodeList == null
					|| decisionTree.childrenNodeList.size() <= 0) {
				return decisionTree.featureName;
			}
			// ���ӽڵ㲻Ϊ��,���ж�����ֵ�ҵ��ӽڵ�
			for (int i = 0; i < featureList.size() - 1; i++) {
				// �ҵ���ǰ�����±�
				if (featureList.get(i).equals(decisionTree.featureName)) {
					// �õ�������������ֵ
					String featureValue = testDataList.get(i);
					// ���ӽڵ����ҵ����д�����ֵ�Ľڵ�
					Node childNode = null;
					for (Node cn : decisionTree.childrenNodeList) {
						if (cn.lastFeatureValue.equals(featureValue)) {
							childNode = cn;
							break;
						}
					}
					// ���û���ҵ��˽ڵ�,��˵��ѵ������û���ҵ�����ڵ������ֵ
					if (childNode == null) {
						System.out.println("û���ҵ�������ֵ������");
						return "û���ҵ�������ֵ������";
					}
					decisionTree = childNode;
					break;
				}
			}
		}
		return "ERROR";
	}
	
	
	
	
	/**
	 * ����������
	 * 
	 * @param dataSetList
	 *            ���ݼ�
	 * @param featureIndexList
	 *            ���õ������б�
	 * @param lastFeatureValue
	 *            ����˽ڵ����һ������ֵ
	 * @return
	 */
	private static Node createDecisionTree(List<Integer> dataSetList,
			List<Integer> featureIndexList, String lastFeatureValue) {
		// ���ֻ��һ��ֵ�Ļ�,��ֱ�ӷ���Ҷ�ӽڵ�
		int valueIndex = featureIndexList.get(featureIndexList.size() - 1);// featureIndexList & valueIndex����ʾ5��������Ŀ����ȥ���Ľ��
		// ѡ���һ��ֵ,firstValue��ֵȡ����data.txt�����ұߵġ��������һ�е�ֵ
		String firstValue = tableMap.get(dataSetList.get(0)).get(valueIndex);// ���磺dataSetList=16����ҳ��Ŀ����16��
		// System.out.print(firstValue);//firstValue��ֵȡ����data.txt�����ұߵġ��������һ�е�ֵ
		int firstValueNum = 0;
		for (Integer id : dataSetList) {     //dataSetList ������������id������ʸ�����
			if (firstValue.equals(tableMap.get(id).get(valueIndex))) {
				firstValueNum++;
			}
			// System.out.print("AA"+tableMap.get(id).get(valueIndex));
		}
		// System.out.print(firstValueNum);
		if (firstValueNum == dataSetList.size()) {
			Node node = new Node();
			node.lastFeatureValue = lastFeatureValue;
			node.featureName = firstValue;
			node.childrenNodeList = null;
			return node;
		}
		// ��������������ʱ����ֵ��û����ȫ��ͬ,���ض�������Ľ��
		if (featureIndexList.size() == 1) {
			Node node = new Node();
			node.lastFeatureValue = lastFeatureValue;
			node.featureName = majorityVote(dataSetList);
			node.childrenNodeList = null;
			return node;
		}
		// !!!!!!!!!!!!!!!!!!!��ʼ�Ķ��ó���Ϣ����Ĵ��룡����������������ʵ�������������!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		// �����Ϣ������������
		 int bestFeatureIndex =
		 chooseBestFeatureToSplit(dataSetList,featureIndexList);
		// �����Ϣ�������������
		//int bestFeatureIndexRate = chooseBestFeatureToSplitRate(dataSetList,featureIndexList);
		// �õ���������ȫ�ֵ��±�
		int realFeatureIndex = featureIndexList.get(bestFeatureIndex);
		//int realFeatureIndexRate = featureIndexList.get(bestFeatureIndexRate);
		 String bestFeatureName = featureList.get(realFeatureIndex);
		//String bestFeatureNameRate = featureList.get(realFeatureIndexRate);
		// ���������
		Node node = new Node();
		node.lastFeatureValue = lastFeatureValue;
		node.featureName = bestFeatureName; //����Ϣ����Ϊ���� ���������
		//node.featureName = bestFeatureNameRate; // ����Ϣ�����Ϊ���� ���������
		// �õ���������ֵ�ļ���
		 List<String> featureValueList =
		 featureValueTableList.get(realFeatureIndex);//������г����������ҳhttps://blog.csdn.net/weixin_39274753/article/details/79709830
		//List<String> featureValueListRate = featureValueTableList
		//		.get(realFeatureIndexRate);
		// ɾ��������
		 featureIndexList.remove(bestFeatureIndex);
		//featureIndexList.remove(realFeatureIndexRate);
		// ������������ֵ,�������ݼ�,Ȼ��ݹ�õ��ӽڵ�

		 for(String fv : featureValueList){//������Ϣ��������� �õ��ݹ��ӽڵ� // �õ������ݼ�
		 List<Integer> subDataSetList =
		 splitDataSet(dataSetList,realFeatureIndex,fv); //��������ݼ�Ϊ�գ���ʹ�ö��������һ���𰸡�
			 if(subDataSetList ==null || subDataSetList.size()<=0)
			 {
			 	Node childNode = new Node();
		 		childNode.lastFeatureValue = fv;
		 		childNode.featureName = majorityVote(dataSetList);
		 		childNode.childrenNodeList = null;
		 		node.childrenNodeList.add(childNode);
		 		break;
			 } // ����ӽڵ� Node
		 Node childNode = createDecisionTree(subDataSetList,featureIndexList,fv);
		 node.childrenNodeList.add(childNode);

//		for (String fv : featureValueListRate) {// ������Ϣ���������� �õ��ݹ��ӽڵ�
//			List<Integer> subDataSetList = splitDataSet(dataSetList,
//					realFeatureIndexRate, fv);
//			if (subDataSetList == null || subDataSetList.size() <= 0) {
//				Node childNode = new Node();
//				childNode.lastFeatureValue = fv;
//				childNode.featureName = majorityVote(dataSetList);
//				childNode.childrenNodeList = null;
//				node.childrenNodeList.add(childNode);
//				break;
//			}
//			Node childNode = createDecisionTree(subDataSetList,
//					featureIndexList, fv);
//			node.childrenNodeList.add(childNode);
		}
		return node;
	}

	/**
	 * ��һ�����ݼ����л���
	 * 
	 * @param dataSetList
	 *            �����ֵ����ݼ�
	 * @param FeatureIndex
	 *            �ڼ�������(�����±�,��0��ʼ)
	 * @param value
	 *            �õ�ĳ������ֵ�����ݼ�
	 * @return
	 */
	private static List<Integer> splitDataSet(List<Integer> dataSetList,
			int FeatureIndex, String value) {
		List<Integer> resultList = new ArrayList<Integer>();
		// System.out.print("AA"+dataSetList);
		for (Integer id : dataSetList) {
			if (tableMap.get(id).get(FeatureIndex).equals(value)) {
				resultList.add(id);// resultList=һ������ͬ���Եı�ţ���Ŵ��㿪ʼ��
			}
			// System.out.print("AA"+tableMap.get(id).get(FeatureIndex));
			// System.out.print("BB"+value);
		}
		return resultList;
	}

	/**
	 * ��������õ����ִ��������Ǹ�ֵ
	 * @param dataSetList
	 * @return
	 */
	private static String majorityVote(List<Integer> dataSetList) {
		// �õ����
		int resultIndex = tableMap.get(dataSetList.get(0)).size() - 1;
		// System.out.print(resultIndex);
		Map<String, Integer> valueMap = new HashMap<String, Integer>();
		for (Integer id : dataSetList) {
			String value = tableMap.get(id).get(resultIndex);
			Integer num = valueMap.get(value);
			if (num == null || num == 0) {
				num = 0;
			}
			valueMap.put(value, num + 1);
		}
		int maxNum = 0;
		String value = " ";

		for (Map.Entry<String, Integer> entry : valueMap.entrySet()) {
			if (entry.getValue() > maxNum) {
				maxNum = entry.getValue();
				value = entry.getKey();
				// System.out.print("AA"+maxNum);
				// System.out.print("BB"+value);
			}
		}
		return value;
	}

	/**
	 * ��ָ���ļ���������ѡ��һ���������(��Ϣ�������)���ڻ������ݼ�
	 * 
	 * @param dataSetList
	 * @return ��������������±�
	 */
	private static int chooseBestFeatureToSplit(List<Integer> dataSetList,
			List<Integer> featureIndexList) {    // �˴������õ������������û���õ������Ǹ������Ϣ����ȵĴ���
		double baseEntropy = calculateEntropy(dataSetList);
		double bestInformationGain = 0;
		// double bestGainRate=0;
		// double GainRate=0;//��������Ϣ�������Ϊ0
		int bestFeature = -1;
		// ѭ��������������
		System.out.println();
		for (int temp = 0; temp < featureIndexList.size() - 1; temp++) {
			int i = featureIndexList.get(temp);
			// �õ���������
			List<String> featureValueList = new ArrayList<String>();
			for (Integer id : dataSetList) {
				String value = tableMap.get(id).get(i);
				featureValueList.add(value);
			}
			Set<String> featureValueSet = new HashSet<String>();
			featureValueSet.addAll(featureValueList);

			// �õ��˷����µ���
			double newEntropy = 0;
			for (String featureValue : featureValueSet) {
				List<Integer> subDataSetList = splitDataSet(dataSetList, i,
						featureValue);
				double probability = subDataSetList.size() * 1.0
						/ dataSetList.size();
				newEntropy += probability * calculateEntropy(subDataSetList);
				// System.out.println("000"+subDataSetList);//�������ÿһ�������ڸ������µı��
				// System.out.println(probability);
				// System.out.println(newEntropy);
			}

			// System.out.println();

			// for (String f : featureList) { // ѭ�������������
			// System.out.print("�����ǣ�" + f);
			// featureList.remove(f);// ���һ��֮��ɾ������һ�μ�������һ��
			// break; // ��һ�ξ�Ҫ����ѭ��
			// }
			for (int f = 0; f < featureList.size(); f++) {
				System.out.print("�����ǣ�" + featureList.get(i));
				break;
			}
			// �õ���Ϣ����
			double informationGain = baseEntropy - newEntropy;
			// GainRate=informationGain/newEntropy;
			// System.out.print("�������������ǣ�" + newEntropy + ",����������Ϣ���棺"+
			// informationGain+",����������Ϣ����ȣ�"+GainRate);
			System.out.print("�������������ǣ�" + newEntropy + ",����������Ϣ���棺"
					+ informationGain);
			System.out.println();

			// �õ���Ϣ�������������±�
			if (informationGain > bestInformationGain) {
				bestInformationGain = informationGain;
				bestFeature = temp;
			}else{break;}
			// �õ���Ϣ��������������±�
			// if(GainRate>bestGainRate){
			// bestGainRate=GainRate;
			// bestFeature = temp;
			// }
		}
		// System.out.println("�����Ϣ�����ֵ��" + bestInformationGain);
		// System.out.println("�����Ϣ����ȣ�" + GainRate);
		return bestFeature;
	}

	/**
	 * ��ָ���ļ���������ѡ��һ���������(��Ϣ��������)���ڻ������ݼ�
	 * 
	 * @param dataSetList
	 * @return ��������������±�
	 */
	private static int chooseBestFeatureToSplitRate(List<Integer> dataSetList,
			List<Integer> featureIndexList) {
		double baseEntropy = calculateEntropy(dataSetList);
		// double bestInformationGain = 0;
		double bestGainRate = 0;
		double GainRate = 0;// ��������Ϣ�������Ϊ0
		int count = 0;// ��¼ÿһ��������Ϊ0�ĸ�������ĳһ���������� ���ض�Ϊ0����ֹͣ����
		int temp = 0;
		int bestFeature = -1;
		// ѭ��������������
		System.out.println();
		System.out.print("���������ܵ����ǣ�" + baseEntropy + "         ");
		int a = featureIndexList.size() - 1;// ÿһ�ֵ�������Ŀ
		System.out.println("��һ������������" + a);
		// for (int temp = 0; temp < featureIndexList.size() - 1; temp++) {
		while (temp < featureIndexList.size() - 1) {
			int i = featureIndexList.get(temp);
			// �õ���������
			List<String> featureValueList = new ArrayList<String>();
			for (Integer id : dataSetList) {
				String value = tableMap.get(id).get(i);
				featureValueList.add(value);
			}
			Set<String> featureValueSet = new HashSet<String>();
			featureValueSet.addAll(featureValueList);

			// �õ��˷����µ���
			double newEntropy = 0;
			for (String featureValue : featureValueSet) {
				List<Integer> subDataSetList = splitDataSet(dataSetList, i,
						featureValue);
				double probability = subDataSetList.size() * 1.0
						/ dataSetList.size();
				newEntropy += probability * calculateEntropy(subDataSetList);
				// System.out.println("000"+subDataSetList);//�������ÿһ�������ڸ������µı��
				// System.out.println(probability);
				// System.out.println(newEntropy);
			}
			// for (String f : featureList) { // ѭ�������������
			// System.out.print("�����ǣ�" + f);
			// featureList.remove(f);// ���һ��֮��ɾ������һ�μ�������һ��
			// break; // ��һ�ξ�Ҫ����ѭ��
			// }
			for (int f = 0; f < featureList.size(); f++) {
				System.out.print("�����ǣ�" + featureList.get(i));
				break;
			}
			// �õ���Ϣ����

			double informationGain = baseEntropy - newEntropy;
			GainRate = informationGain / newEntropy;
			if (newEntropy == 0) {
				count++;
				GainRate = 0.000000000001;
			}
			System.out.print("�������������ǣ�" + newEntropy + ",����������Ϣ���棺"
					+ informationGain + ",����������Ϣ����ȣ�" + GainRate);
			// System.out.print("�������������ǣ�" + newEntropy + ",����������Ϣ���棺"+
			// informationGain);
			System.out.println();

			// �õ���Ϣ�������������±�
			// if (informationGain > bestInformationGain) {
			// bestInformationGain = informationGain;
			// bestFeature = temp;
			// }

			// �õ���Ϣ��������������±�
			if (GainRate > bestGainRate/* &Double.isInfinite(GainRate)==true */) {
				bestGainRate = GainRate;
				bestFeature = temp;
			}

			if (count == a) {
				// System.out.print("@@@@@@@@@@@@@@@@"+count+"  "+a);
				temp = featureIndexList.size() + 1;// ??????��仰ûʲô�ã�����������������������������
				// return 0;
			} else {
				temp++;
			}

		}
		// System.out.println("�����Ϣ�����ֵ��" + bestInformationGain);
		System.out.println("�����Ϣ����ȣ�" + bestGainRate);
		return bestFeature;
	}

	/**
	 * ������
	 * 
	 * @param dataSetList
	 * @return
	 */
	private static double calculateEntropy(List<Integer> dataSetList) {
		if (dataSetList == null || dataSetList.size() <= 0) {
			return 0;
		}
		// �õ����Ի����Ľ��
		int resultIndex = tableMap.get(dataSetList.get(0)).size() - 1;// ���� ��Ŀ   ��˳����
		Map<String, Integer> valueMap = new HashMap<String, Integer>();
		for (Integer id : dataSetList) {
			String value = tableMap.get(id).get(resultIndex); // value��������������
			// System.out.print("A"+value+" ");//value ͨ��tableMap�İ�����ͳ��������17������������������õõ���
			                               //����ֻ���ټ�һ��tableMap1����������ޡ�ȱʧֵ������������,�õ�������ȥ��������
			Integer num = valueMap.get(value);
			if (num == null || num == 0) {
				num = 0;
			}
			valueMap.put(value, num + 1); // �õ����Ľ�������Ը������������
		}
//���Կ�����������Եĸ���		 System.out.print("@@"+valueMap); //�õ����Ľ�������Ը������������
		                                 //!BUT�����������⣬���ǰ�ȱʧֵ��Ӧ�Ķ������Լ������ڣ���Ҫ�ĸĸ�
		
		double entropy = 0;
		for (Map.Entry<String, Integer> entry : valueMap.entrySet()) {
			//if (!entry.getValue().equals("0")) {// 20180105
														// ͻ�ƿ�!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
				// continue;//��if���������£�ִ��continue���Ǿ��ǰ�null�����ݲ������ȥ�ˣ��øĹ�ʽ��
			    // ������
				double prob = entry.getValue() * 1.0 / dataSetList.size();
				entropy -= prob * Math.log10(prob) / Math.log10(2);// Java��ûlog2�ĺ������ô��ְ취����
		}
		//}
		// System.out.print("?"+dataSetList.size()+"��"+valueMap.entrySet());//20180104��취ɾȥnull��Ӧ������ֵ���ø�
		return entropy;
	}

	/**
	 * ��ʼ������
	 * 
	 * @param file
	 * @throws IOException
	 * @throws Exception
	 */
	private static void readOriginalData(File file) throws IOException {
		int index = 0;
		int index1= 0;
		try {
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			String line;
			while ((line = br.readLine()) != null)// ���ݲ�Ϊ��
			{
				// �õ���������
				if (line.startsWith("@feature")) {
					line = br.readLine();
					// System.out.print("BBBBB"+line);
					String[] row = line.split(",");
					for (String s : row) {
						featureList.add(s.trim());// trim()ȥ���ַ����������ߵĿո��м�Ĳ�ȥ��
					}
				}
				// �õ�����ֵ
				else if (line.startsWith("@data")) {
					while ((line = br.readLine()) != null) {
						if (line.equals("")) {// ȥ�ո�
							continue;
						}
						String[] row = line.split(",");
						//for (int i = 0; i < row.length; i++) {
							// if(!row[i].equals("null")){//20180104���ӵĴ��룬��취ȥ��
							// ȱʧֵ,but��û��Ч
							// continue;
							// }
						//}
						// System.out.println("BBBBB"+line);//�˴���line�����������ֵ
						// if (row.length != featureList.size()) {//
						// row����ŵ������ݣ������ԣ���featureList����ŵ�����������
						// throw new Exception("�б����ݺ�������Ŀ��һ��");
						// }

						List<String> tempList = new ArrayList<String>();
						List<String> tempList1 = new ArrayList<String>();
						// for(String s :row){
						for (int i = 0; i < row.length; i++) {
							// if (s.trim().equals("")) {//
							// ȥ�ո�Ȼ���""��ȣ���ͬ����true;����ͬ���׳��쳣
							// throw new Exception("�б����ݲ���Ϊ��");
							// }
							if (row[i].equals("null")) {// 20180105:�˴����ļ��е�null�ĳ���0������0��Ȼ�Ǹ��ַ���
								row[i] = "0";//�����ļ���ľ���������Ϣ����
							}
							tempList.add(row[i]);
						}
	
						for(int j=0;j<row.length;j++){//��ѭ��Ϊ�˵õ�tableMap1��׼��
							while(row[j].equals("0")){
								j++;//������������������Ǹ�ֵ��ȱʧֵ����ôpass����������һ��
							}
							tempList1.add(row[j]);
						}
						tableMap.put(index++, tempList);
						tableMap1.put(index1++, tempList1);
						// System.out.println("?"+tableMap);//tableMap��¼��ÿһ�е�����ֵ,����null����0zhiȱʧֵ
					}
					

//!!!!!!!!!!!!!!!!!!!!!!!!!!!					// ���� tableMap   �õ�����ֵ�б�
					Map<Integer, Set<String>> valueSetMap = new HashMap<Integer, Set<String>>();
					for (int i = 0; i < featureList.size(); i++) {
						valueSetMap.put(i, new HashSet<String>());
					}
					for (Map.Entry<Integer, List<String>> entry : tableMap
							.entrySet()) {// ʹ��Map.Entry�࣬������ͬһʱ��õ�����ֵ
						List<String> dataList = entry.getValue();// dataList��������е�����
						for (int i = 0; i < dataList.size(); i++) {// ÿһ������(����)�ĸ���:7��
							if (!dataList.get(i).equals("0")) {// 20180106ʹvalueSetMap��ֻ�����˹ؼ�����
								valueSetMap.get(i).add(dataList.get(i));
							}
						}
					}
					// System.out.println("QQQ "+valueSetMap);//!!!!!valueSetMap��ֻ�����˹ؼ����ԣ��Ѿ���null����0��ȥ����!!!!
					for (Map.Entry<Integer, Set<String>> entry : valueSetMap
							.entrySet()) {
						List<String> valueList = new ArrayList<String>();
						for (String s : entry.getValue()) {
							// if(!s.equals("null"))//��仰����Ч
							valueList.add(s);// s��ÿһ�е�����ֵ(���ظ���)
							// System.out.println("AAA "+s);//��Ϊ��456�仰��s���Ѿ�������null����0������ȱʧֵ�ˣ�������
						}
						featureValueTableList.add(valueList);
						// System.out.println("AAA "+valueList);//ÿһ�д��ڵ�����ֵ��ʶ�����г�����
					}
					// System.out.println("BBB "+featureValueTableList);//��������ֵ�Ѿ�����ɾ���ÿһ��������ֻ���и��еĵ�������
				} else {
					continue;
				}
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// �ѽ��д��xml�ļ�
	public static void writeToXML(Node node, String filename) {
		// ����xml
		try {
			JAXBContext context = JAXBContext.newInstance(Node.class);
			Marshaller marshaller = context.createMarshaller();

			File file = new File(filename);
			if (file.exists() == false) {
				if (file.getParent() == null) {
					file.getParentFile().mkdirs();// ����һ��Ŀ¼
				}
				file.createNewFile();
			}

			marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8"); // ���ñ����ַ���
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);// ��ʽ��XML������з��к�����

			marshaller.marshal(node, System.out);// ��ӡ������̨

			FileOutputStream fos = new FileOutputStream(file);
			marshaller.marshal(node, fos);// Marshaller �ฺ����� Java ���������л��� XML
											// ���ݵĹ���
			fos.flush();
			fos.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
//!!!!!!!!!!!!!!!!������!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!������!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	public static void main(String[] args) throws IOException {
		// ��ʼ������                                              //src�ļ�����test_malicious.txt�ļ��е������ݳ���30�������ݳ����٣����Կ�������
		//readOriginalData(new File("src/data-rk.txt"));          //src�ļ����е�test30.txt�ļ�����31���ȵ�3054������&3054������  ��������̫�࣬���Բ���
		readOriginalData(new File("src/test_malicious2.txt"));    //src�ļ����е�test.txt�ļ�����129���ȵ�3054������&3054������
		System.out.print("�����ǣ�");                           //src�ļ����е�test1.txt�ļ�����91���ȵ�3054������&3054������
		for (String f : featureList) {
			System.out.print(f + ",");//�������
		}
		System.out.println("\n");// ��������������ݼ��һ��

		// ������ݼ����б�
		// �������ѵ���� data.txt �е�ԭʼ������Ϣ
		List<Integer> tempDataList = new ArrayList<Integer>();
		List<String> tempValueList = new ArrayList<String>();
		System.out.println("�ļ��е�ԭʼ����ֵ��");
		for (Map.Entry<Integer, List<String>> entry : tableMap.entrySet())// ����map�еļ�ֵ��
		{
			System.out.print(entry.getKey() + ","); // �����0~16
			for (String s : entry.getValue()) {//getValue()��������ֵ
				System.out.print(s + ","); // ���ֵ
				tempValueList.add(s);
			}
			System.out.println();
			tempDataList.add(entry.getKey());
			//System.out.println("??"+tempValueList);
			//System.out.println("@@"+tempDataList);
		}
		System.out.println();

		//!!!!!!!!!!���valueMap1 ȥ��ȱʧֵ֮��ı������
		List<Integer> tempDataList1 = new ArrayList<Integer>();
		List<String> tempValueList1 = new ArrayList<String>();
		System.out.println("tableMap1�е�����ֵ��");
		for (Map.Entry<Integer, List<String>> entry1 : tableMap1.entrySet())// ����map�еļ�ֵ��
		{
			System.out.print(entry1.getKey() + ",");   // �����0~16
			for (String s : entry1.getValue()) {   //getValue()��������ֵ
				System.out.print(s + ",");    // ���ֵ
				tempValueList1.add(s);
			}
			System.out.println();
			tempDataList1.add(entry1.getKey());
		}
		System.out.println();
		
		
		// �õ��������б�
		// Ϊ�����������׼��
		List<Integer> featureIndexList = new ArrayList<Integer>();
		for (int i = 0; i < featureList.size(); i++) {
			featureIndexList.add(i);
		}

		// ����ڲ�ʹ���κ�����������¼������
		double baseEntropy = calculateEntropy(tempDataList);
		System.out.println();
		System.out.print("�ڲ�ʹ���κ�����������¼������:" + baseEntropy);
		System.out.println();

		// ���ÿ���������ص�ֵ����Ϣ����
		// int FeatureEntropy = chooseBestFeatureToSplit(tempDataList,
		// featureIndexList);
		// System.out.println(FeatureEntropy);

		// ��������жϽ��
		// String ma=majorityVote(tempDataList);
		// System.out.println(ma);

		// ���������
		Node decisionTree = createDecisionTree(tempDataList, featureIndexList,
				null);
		// ������ļ���
		String outputFilePath = "E:/id3-calculate.xml";
		//String outputFilePath = "E:/test-xssed-normal.xml";
		writeToXML(decisionTree, outputFilePath);
		
		//ѵ�����֮������һ�����ݣ�����Ԥ��
		//math,parseint,indexof,gettime,settime,regexp


		/**/File YZFile = new File("src/yanzheng.txt");   //yanzheng�ļ������У������������������ж�
		InputStreamReader rdFile = new InputStreamReader(new FileInputStream(YZFile));
		BufferedReader bfReader = new BufferedReader(rdFile);
		String txtline = null;
		int i=1;
		while ((txtline=bfReader.readLine())!=null) {
			txtline = txtline+"\n";
			System.out.println("�жϽ��"+i+":"
					+ getDTAnswer(decisionTree, featureList,
					/*Arrays.asList("sip=,0.0.0.0,iqq=,0,sbiz=,vod_flash_p0p,sop=,click,ista=,0,ity=,0,iflow=,0,guid=,0c0b0bf0be0bb0de0eec0d0d,playno=,0d0a0daed0bacc0afd0e0cae0c0ece,progurl=,g0b0afsd.0.0,cdn=,dsip=,p0pver=,0,curtime=,0,seekcount=,reporttype=,0,srvip=,srvport=,0,srvtype=,0,delayinfo=,0,trycount=,0,loadingtime=,0,blockcount=,blocktime=,errorcode=,0,seekblocktime=,httpdownlandspeed=,0,httpdownsum=,0,udpdownlandspeed=,0,udpdownsum=,0,updataspeed=,0,udpu,>,>,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0".split(","))));
					 */Arrays.asList(txtline.split(","))));
			i++;
		}

		//data.txt��Ĵ���֤���ݣ���ʵ�Ƕ���ģ�random,function,Date,nl,apply
//						Arrays.asList("ct_orig_url=,arena,>,<script>,alert(,0,),</script>,<iframe,src=,http://u,>,</iframe>,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0".split(","))));
//						Arrays.asList("_=,0,list=,sz0,sh0,sz0,sz0,sz0,sz0,sh0,sz0,sz0,sz0,rb0,i0,m0,c0,jd0,sr0,bu0,ru0,ag0,au0,hf_cad,hf_cl,hf_gc,hf_si,hf_s,hf_bo,hf_c,hf_w,hf_ahd,hf_oil,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0".split(","))));
//						Arrays.asList("searchconfiguration=,0bd0a0ab0d0b0cd0b0f0aebf0f0ade0f0b0a0a0b0b0e0f0b0b0b0e0c0fcd0a0e0ccbde0f0fe0e0f0ef0fffd0c0a0fa0c0cd0cb0a0ea0ccb0c0d0bc0d0dc0ead0b0d0a0e0f0a0d0b0f0c0c0bde0fb0c0ea0a0da0dd0d0c0c0a0cbaca0e0ec0d0e0bea0b0bc0c0ace0a0c0f0be0ecf0cf0a0cc0dd0d0d0b0c0afe0df0ff0ffdf0db0b0ceb0fc0a0da0ffb0b0de0fe0aed0beba0ce0d0dcc0b0ffca0cd0c,searchterm=,>,<script>,alert(,0ssi0_tr,),</script>,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0".split(","))));
// 						Arrays.asList("hid=,sgpy,windows,generic,device,id,v=,0.0.0.0,brand=,0,platform=,0,ifbak=,0,ifmobile=,0,ifauto=,0,type=,0,filename=,sgim_privilege.zip,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0".split(","))));
	}
//����Ϊ129��xss���Ĳ������ݣ�ct_orig_url=,arena,>,<script>,alert(,0,),</script>,<iframe,src=,http://u,>,</iframe>,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
//����Ϊ129��normal���Ĳ������ݣ�_=,0,list=,sz0,sh0,sz0,sz0,sz0,sz0,sh0,sz0,sz0,sz0,rb0,i0,m0,c0,jd0,sr0,bu0,ru0,ag0,au0,hf_cad,hf_cl,hf_gc,hf_si,hf_s,hf_bo,hf_c,hf_w,hf_ahd,hf_oil,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
//����Ϊ90��xss���Ĳ������ݣ�searchconfiguration=,0bd0a0ab0d0b0cd0b0f0aebf0f0ade0f0b0a0a0b0b0e0f0b0b0b0e0c0fcd0a0e0ccbde0f0fe0e0f0ef0fffd0c0a0fa0c0cd0cb0a0ea0ccb0c0d0bc0d0dc0ead0b0d0a0e0f0a0d0b0f0c0c0bde0fb0c0ea0a0da0dd0d0c0c0a0cbaca0e0ec0d0e0bea0b0bc0c0ace0a0c0f0be0ecf0cf0a0cc0dd0d0d0b0c0afe0df0ff0ffdf0db0b0ceb0fc0a0da0ffb0b0de0fe0aed0beba0ce0d0dcc0b0ffca0cd0c,searchterm=,>,<script>,alert(,0ssi0_tr,),</script>,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
//����Ϊ90��normal���Ĳ������ݣ�hid=,sgpy,windows,generic,device,id,v=,0.0.0.0,brand=,0,platform=,0,ifbak=,0,ifmobile=,0,ifauto=,0,type=,0,filename=,sgim_privilege.zip,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
//����Ϊ30��xss���Ĳ������ݣ�option=,com_wdshop,view=,userinfo,ajax_json=,ajax_fill_city_state,format=,raw,zip=,>,</style>,</script>,<script>,alert(,document.cookie,),</script>,0,0,0,0,0,0,0,0,0,0,0,0,0
//����Ϊ30��normal���Ĳ������ݣ�cgi=,cgi_farm_xiaotan_index,type=,0,time=,0,uin=,0,domain=,nc.qzone.qq.com,rate=,0,code=,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
}
