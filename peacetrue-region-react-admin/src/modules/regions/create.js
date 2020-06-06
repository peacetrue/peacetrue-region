import {Create, SimpleForm, TextInput,} from 'react-admin';

export const RegionCreate = (props) => {
    console.info('RegionCreate:', props);
    return (
        < Create
    {...
        props
    }
    title = {`新建${props.options.label}`
}>
<
    SimpleForm >
    < TextInput
    label = {'编码'}
    source = "code"
    validate = {required()}
    />
    < TextInput
    label = {'名称'}
    source = "name"
    validate = {required()}
    />
    < TextInput
    label = {'备注'}
    source = "remark"
    validate = {required()}
    />
    < TextInput
    label = {'上一级主键'}
    source = "parentId"
    validate = {required()}
    />
    < /SimpleForm>
    < /Create>
)
    ;
};
