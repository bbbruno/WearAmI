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
#   RequiredState						                        #
#   AchievedState						                        #
#   RequiredResource						                    #
#   All AllenIntervalConstraint types                           #
#   '[' and ']' should be used only for constraint bounds       #
#   '(' and ')' are used for parsing                            #
#                                                               #
#################################################################

###############################
# Domain: testLocationPosture #
#################################################
# Sensors:                                      #
# 01. Location (PIR)                            #
# 03. Posture (waist-placed fall detector)      #
#                                               #
# Recognized Activities:                        #
# (debug mode)                                  #
# a. Seated                                     #
# b. Standing                                   #
# c. FallInLivingroom							#
# d. FallInKitchen								#
#################################################


(Domain TestLOcationPosture)

(Sensor Location)
(Sensor Posture)
(Sensor FallEvent)

(ContextVariable Human)

##########
# Seated #
##########
(SimpleOperator
 (Head Human::Seated())
 (RequiredState req1 Posture::Sitting())
 (Constraint Equals(Head,req1))
)

############
# Standing #
############
(SimpleOperator
 (Head Human::Standing())
 (RequiredState req1 Posture::Standing())
 (Constraint Equals(Head,req1))
)

####################
# FallInLivingroom #
####################
(SimpleOperator
 (Head Human::FallInLivingroom())
 (RequiredState req1 FallEvent::true())
 (RequiredState req2 Location::Livingroom())
 (Constraint Equals(Head,req1))
 (Constraint During(Head,req2))
)

#################
# FallInKitchen #
#################
(SimpleOperator
 (Head Human::FallInKitchen())
 (RequiredState req1 FallEvent::true())
 (RequiredState req2 Location::Kitchen())
 (Constraint Equals(Head,req1))
 (Constraint During(Head,req2))
)