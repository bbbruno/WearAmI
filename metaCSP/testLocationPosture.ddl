##################
# Reserved words #
#################################################################
#                                                               #
#   Head                                                        #
#   Resource                                                    #
#   Sensor                                                      #
#   ContextVariable                                             #
#   SimpleOperator                                              #
#   SimpleDomain                                                #
#   Constraint                                                  #
#   RequiredState                                               #
#   AchievedState                                               #
#   RequiredResource                                            #
#   All AllenIntervalConstraint types                           #
#   '[' and ']' should be used only for constraint bounds       #
#   '(' and ')' are used for parsing                            #
#                                                               #
#################################################################

###############################
# Domain: testLocationPosture #
#################################################
# Sensors:                                      #
# 01.  Location (PIR)                           #
# 03a. Posture (waist-placed IMU)               #
# 03b. FallEvent (waist-placed IMU)             #
# 04a. KitchenChairWindow (pressure sensor)     #
#                                               #
# Recognized Activities:                        #
# (debug mode)                                  #
# a. Seated                                     #
# b. ConfusingSeated                            #
# c. FallInLivingroom                           #
# d. FallInKitchen                              #
#################################################


(Domain TestLOcationPosture)

(Sensor Location)
(Sensor Posture)
(Sensor FallEvent)
(Sensor KitchenChairWindow)

(ContextVariable Human)

##########
# Seated #
##########
(SimpleOperator
 (Head Human::Seated())
 (RequiredState req1 Posture::Sitting())
 (RequiredState req2 KitchenChairWindow::true())
 (Constraint During(Head,req1))
 (Constraint During(Head,req2))
)

###################
# ConfusingSeated #
###################
(SimpleOperator
 (Head Human::ConfusingSeated())
 (RequiredState req1 Posture::Standing())
 (RequiredState req2 KitchenChairWindow::true())
 (Constraint During(Head,req1))
 (Constraint During(Head,req2))
)

####################
# FallInLivingroom #
####################
(SimpleOperator
 (Head Human::FallInLivingroom())
 (RequiredState req1 FallEvent::true())
 (RequiredState req2 Location::Livingroom())
 (Constraint During(Head,req1))
 (Constraint During(Head,req2))
)

#################
# FallInKitchen #
#################
(SimpleOperator
 (Head Human::FallInKitchen())
 (RequiredState req1 FallEvent::true())
 (RequiredState req2 Location::Kitchen())
 (Constraint During(Head,req1))
 (Constraint During(Head,req2))
)
