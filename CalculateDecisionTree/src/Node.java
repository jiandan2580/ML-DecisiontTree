

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "Node")
@XmlType(name = "Node")
public class Node
{
	/**
	 * ����˽ڵ������ֵ
	 */
	public String lastFeatureValue;
	/**
	 * �˽ڵ���������ƻ��
	 */
	public String featureName;
	/**
	 * �˽ڵ�ķ����ӽڵ�
	 */
	public List<Node> childrenNodeList = new ArrayList<Node>();

	@Override
	public String toString()
	{
		return "Node [lastFeatureValue=" + lastFeatureValue + ", featureName=" + featureName + ", childrenNodeList=" + childrenNodeList + "]";
	}

}
