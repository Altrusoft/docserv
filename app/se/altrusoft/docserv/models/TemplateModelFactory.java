package se.altrusoft.docserv.models;

import java.util.Map;

public class TemplateModelFactory {
	
	private Map<String, TemplateModel> templates;
	
	public TemplateModel getTemplateModel(String templateName) {
		return templates.get(templateName).getClone();
	}
	
	public Map<String, TemplateModel> getTemplates() {
		return templates;
	}

	public void setTemplates(Map<String, TemplateModel> templates) {
		this.templates = templates;
	}

//	public static Optional<TemplateModel> getTemaplateModelN(String name) {
//		Class<? extends TemplateModel> clazz = null;
//		Optional<TemplateModel> result = Optional.empty();
//		try {
//			clazz = Class.forName("se.altrusoft.docserv.models." + name).asSubclass(TemplateModel.class);
//			result = Optional.of(clazz.newInstance());
//		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//		}
//		return result;
//	}
}
