import {BasicColumn} from '/@/components/Table';
import {FormSchema} from '/@/components/Table';
import { rules} from '/@/utils/helper/validator';
import { render } from '/@/utils/common/renderUtils';
//列表数据
export const columns: BasicColumn[] = [
   {
    title: '培训记录',
    align:"center",
    dataIndex: 'recordId'
   },
   {
    title: '培训名称',
    align:"center",
    dataIndex: 'trainingName'
   },
   {
    title: '培训成绩',
    align:"center",
    dataIndex: 'trainingScore'
   },
   {
    title: '取得证书',
    align:"center",
    dataIndex: 'getCertificate_dictText'
   },
   {
    title: '证书名称',
    align:"center",
    dataIndex: 'certificateName'
   },
   {
    title: '证书编号',
    align:"center",
    dataIndex: 'certificateNumber'
   },
];
//查询数据
export const searchFormSchema: FormSchema[] = [
	{
      label: "培训名称",
      field: 'trainingName',
      component: 'Input',
      colProps: {span: 6},
 	},
	{
      label: "取得证书",
      field: 'getCertificate',
      component: 'JDictSelectTag',
      componentProps:{
          dictCode:"get_certificate_result"
      },
      colProps: {span: 6},
 	},
	{
      label: "证书名称",
      field: 'certificateName',
      component: 'Input',
      colProps: {span: 6},
 	},
];
//表单数据
export const formSchema: FormSchema[] = [
  {
    label: '培训记录',
    field: 'recordId',
    component: 'Input',
  },
  {
    label: '培训名称',
    field: 'trainingName',
    component: 'Input',
  },
  {
    label: '培训成绩',
    field: 'trainingScore',
    component: 'Input',
  },
  {
    label: '取得证书',
    field: 'getCertificate',
    component: 'JDictSelectTag',
    componentProps:{
        dictCode:"get_certificate_result"
     },
  },
  {
    label: '证书名称',
    field: 'certificateName',
    component: 'Input',
  },
  {
    label: '证书编号',
    field: 'certificateNumber',
    component: 'Input',
  },
	// TODO 主键隐藏字段，目前写死为ID
	{
	  label: '',
	  field: 'id',
	  component: 'Input',
	  show: false
	},
];



/**
* 流程表单调用这个方法获取formSchema
* @param param
*/
export function getBpmFormSchema(_formData): FormSchema[]{
  // 默认和原始表单保持一致 如果流程中配置了权限数据，这里需要单独处理formSchema
  return formSchema;
}