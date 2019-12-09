package com.jiang.myeasyutils.adapters;

public class Test {


    /**
     * mName : focusPoints
     * entity : {"name":"liweibo","age":18}
     */

    private String mName;
    private EntityBean entity;

    public String getMName() {
        return mName;
    }

    public void setMName(String mName) {
        this.mName = mName;
    }

    public EntityBean getEntity() {
        return entity;
    }

    public void setEntity(EntityBean entity) {
        this.entity = entity;
    }

    public static class EntityBean {
        /**
         * name : liweibo
         * age : 18
         */

        private String name;
        private int age;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }
    }
}
