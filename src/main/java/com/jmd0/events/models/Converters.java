package com.jmd0.events.models;

/*
 * This class contains methods to convert between UserEntity and UserModel
 * It is used to convert between the database entity and the model used by the controller
 */
public class Converters {
    
    public static UserEntity userModelToUserEntity(UserModel userModel) {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(Long.parseLong(userModel.getId()));
        userEntity.setusername(userModel.getusername());
        userEntity.setPassword(userModel.getPassword());
        return userEntity;
    }

    public static UserModel userEntityToUserModel(UserEntity userEntity) {
        UserModel userModel = new UserModel();
        userModel.setId(String.valueOf(userEntity.getId()));
        userModel.setusername(userEntity.getusername());
        userModel.setPassword(userEntity.getPassword());
        return userModel;
    }

    
}
