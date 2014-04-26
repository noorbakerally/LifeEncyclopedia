package com.itspirits.lifeencyclopediaadv.lifeencyclopediaadv.others;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.*;

public class LifeObject {
		
		//the type of the entity
		private String description;
		private String entity;
		private String label;
		
		private String entity_type;
		private String image;
		private String thumbnail;
		private String sound;
		private List <String> scientific_names ;
		private List <String> common_names ;
		private List <LifeObject> direct_subclasses;
		
		private String kingdomName;
		private String phylumName;
		private String className;
		private String orderName;
		private String familyName;
		private String genusName;
		private String speciesName;
		
		
		public String getKingdomName() {
			return kingdomName;
		}
		public void setKingdomName(String kingdomName) {
			this.kingdomName = kingdomName;
		}
		public String getPhylumName() {
			return phylumName;
		}
		public void setPhylumName(String phylumName) {
			this.phylumName = phylumName;
		}
		public String getClassName() {
			return className;
		}
		public void setClassName(String className) {
			this.className = className;
		}
		public String getOrderName() {
			return orderName;
		}
		public void setOrderName(String orderName) {
			this.orderName = orderName;
		}
		public String getFamilyName() {
			return familyName;
		}
		public void setFamilyName(String familyName) {
			this.familyName = familyName;
		}
		public String getGenusName() {
			return genusName;
		}
		public void setGenusName(String genusName) {
			this.genusName = genusName;
		}
		public String getSpeciesName() {
			return speciesName;
		}
		public void setSpeciesName(String speciesName) {
			this.speciesName = speciesName;
		}
		public boolean isLoaded() {
			return loaded;
		}
		public void setLoaded(boolean loaded) {
			this.loaded = loaded;
		}
		public boolean loaded;
		
		public LifeObject(String entity, String entity_type) {
			this.entity = entity;
			this.entity_type = entity_type;
		}
		public LifeObject(String entity, String entity_type,String label) {
			this.entity = entity;
			this.entity_type = entity_type;
			this.label = label;
		}
		public LifeObject() {
			// TODO Auto-generated constructor stub
		}
		public String getDescription() {
			return description;
		}
		public void setDescription(String description) {
			this.description = description;
		}
		public String getEntity() {
			return entity;
		}
		public void setEntity(String entity) {
			this.entity = entity;
		}
		public String getEntity_type() {
			return entity_type;
		}
		public void setEntity_type(String entity_type) {
			this.entity_type = entity_type;
		}
		public String getImage() {
			return image;
		}
		public void setImage(String image) {
			this.image = image;
		}
		public String getThumbnail() {
			return thumbnail;
		}
		public void setThumbnail(String thumbnail) {
			this.thumbnail = thumbnail;
		}
		public String getSound() {
			return sound;
		}
		public void setSound(String sound) {
			this.sound = sound;
		}
		public List<String> getScientific_names() {
			return scientific_names;
		}
		public void setScientific_names(List<String> scientific_names) {
			this.scientific_names = scientific_names;
		}
		public List<String> getCommon_names() {
			return common_names;
		}
		public void setCommon_names(List<String> common_names) {
			this.common_names = common_names;
		}
		public List<LifeObject> getDirect_subclasses() {
			return direct_subclasses;
		}
		public void setDirect_subclasses(List<LifeObject> direct_subclasses) {
			this.direct_subclasses = direct_subclasses;
		}
		public void addDirectSubclass(String entity){
			if (this.direct_subclasses == null){
				this.direct_subclasses = new ArrayList<LifeObject>();
			}
			this.direct_subclasses.add(new LifeObject(entity, entity,entity));
		}
		public void addDirectSubclass(LifeObject directSubclass){
			if (this.direct_subclasses == null){
				this.direct_subclasses = new ArrayList<LifeObject>();
			}
			this.direct_subclasses.add(directSubclass);
		}
		
		public void addCommonNames(String commonName){
			if (this.common_names == null){
				this.common_names = new ArrayList<String>();
			}
			this.common_names.add(commonName);
		}
		public void addScientificNames(String scientificName){
			if (this.scientific_names == null){
				this.scientific_names = new ArrayList<String>();
			}
			this.scientific_names.add(scientificName);
		}
		public String getLabel() {
			return label;
		}
		public void setLabel(String label) {
			this.label = label;
		}
}
