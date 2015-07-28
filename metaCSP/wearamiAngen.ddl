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

#########################
# Domain: WearAmI@Angen #
#################################################
# Sensors:                                      #
# 01. Location (PIR)                            #
# 02. Gesture (smartwatch)                      #
# 03. Posture (waist-placed fall detector)      #
# 04. Chair (pressure sensor)                   #
# 05. Armchair (pressure sensor)                #
# 06. Bed (pressure sensor)                     #
# 07. Knife (proximity sensor under the table)  #
# 08. Fork (proximity sensor under the table)   #
# 09. Plate (proximity sensor under the table)  #
# 10. Glass (proximity sensor under the table)  #
# 11. Bottle (proximity sensor under the table) #
# 12. Oven (switch on the door)                 #
# 13. Fridge (switch on the door)               #
# 14. Cupboard (switch on the door)             #
# 15. MicrowaveOven (switch on the door)        #
#                                               #
# Recognized Activities:                        #
# 1. SitDown (chair)                            #
# 1. SitDown (armchair)                         #
# 1. SitDown (bed)                              #
# 2. StandUp                                    #
# 3. LieDown                                    #
# 4. GetUp                                      #
# 5. Eat (cut)                                  #
# 6. Food::OneBite                              #
# 5. Eat (Food::OneBite)                        #
# 7. Drink::Pour                                #
# 5. Eat (Drink::Pour)                          #
# 8. Drink::OneGulp                             #
# 5. Eat (Drink::OneGulp)                       #
# 9. PrepareMeal (oven)                         #
# 9. PrepareMeal (fridge)                       #
# 9. PrepareMeal (cupboard)                     #
# 9. PrepareMeal (microwave oven)               #
# 10. ClimbStairs                               #
# 11. DescendStairs                             #
# 12. Walk                                      #
#################################################


(Domain WearamiAngen)

(Sensor Location)
(Sensor Gesture)
(Sensor Posture)
(Sensor Chair)
(Sensor Armchair)
(Sensor Bed)
(Sensor Knife)
(Sensor Fork)
(Sensor Plate)
(Sensor Glass)
(Sensor Bottle)
(Sensor Oven)
(Sensor Fridge)
(Sensor Cupboard)
(Sensor MicrowaveOven)

(ContextVariable Human)
(ContextVariable Food)
(ContextVariable Drink)

###################
# SitDown (chair) #
###################
(SimpleOperator
 (Head Human::SitDown())
 (RequiredState req1 Gesture::Sit())
 (RequiredState req2 Posture::Sitting())
 (RequiredState req3 Chair::On())
 (Constraint OverlappedBy(Head,req1))
 (Constraint During(Head,req2))
 (Constraint EndEnd(Head,req3))
)

######################
# SitDown (armchair) #
######################
(SimpleOperator
 (Head Human::SitDown())
 (RequiredState req1 Gesture::Sit())
 (RequiredState req2 Posture::Sitting())
 (RequiredState req3 Armchair::On())
 (Constraint OverlappedBy(Head,req1))
 (Constraint During(Head,req2))
 (Constraint EndEnd(Head,req3))
)

#################
# SitDown (bed) #
#################
(SimpleOperator
 (Head Human::SitDown())
 (RequiredState req1 Gesture::Sit())
 (RequiredState req2 Posture::Sitting())
 (RequiredState req3 Bed::On())
 (Constraint OverlappedBy(Head,req1))
 (Constraint During(Head,req2))
 (Constraint EndEnd(Head,req3))
)

#####################
# StandUp (generic) #
#####################
(SimpleOperator
 (Head Human::StandUp())
 (RequiredState req1 Gesture::Stand())
 (RequiredState req2 Posture::Standing())
 (RequiredState req3 Human::SitDown())
 (Constraint MetByOrOverlappedBy(Head,req1))
 (Constraint Starts(Head,req2))
 (Constraint MetByOrAfter(Head,req3))
)

###########
# LieDown #
###########
(SimpleOperator
 (Head Human::LieDown())
 (RequiredState req1 Gesture::LieDown())
 (RequiredState req2 Posture::Lying())
 (RequiredState req3 Bed::On())
 (RequiredState req4 Location::Bedroom())
 (Constraint OverlappedBy(Head,req1))
 (Constraint During(Head,req2))
 (Constraint EndEnd(Head,req3))
 (Constraint During(Head,req4))
)

#########
# GetUp #
#########
(SimpleOperator
 (Head Human::GetUp())
 (RequiredState req1 Gesture::GetUp())
 (RequiredState req2 Posture::Standing())
 (RequiredState req3 Human::LieDown())
 (RequiredState req4 Location::Bedroom())
 (Constraint OverlappedBy(Head,req1))
 (Constraint Starts(Head,req2))
 (Constraint MetByOrAfter(Head,req3))
 (Constraint During(Head,req4))
)

########################
# Eat (cut) - contains #
########################
(SimpleOperator
 (Head Human::Eat())
 (RequiredState req1 Location::Kitchen())
 (RequiredState req2 Gesture::Cut())
 (RequiredState req3 Knife::On())
 (RequiredState req4 Fork::On())
 (RequiredState req5 Plate::On())
 (Constraint During(Head,req1))
 (Constraint Contains(Head,req2))
 (Constraint During(Head,req3))
 (Constraint During(Head,req4))
 (Constraint During(Head,req5))
)

#########################################
# Food::OneBite (PickUp & PutDown food) #
#########################################
(SimpleOperator
 (Head Food::OneBite())
 (RequiredState req1 Location::Kitchen())
 (RequiredState req2 Posture::Sitting())
 (RequiredState req3 Gesture::PickUp())
 (RequiredState req4 Gesture::PutDown())
 (RequiredState req5 Fork::On())
 (RequiredState req6 Plate::On())
 (Constraint During(Head,req1))
 (Constraint During(Head,req2))
 (Constraint StartedBy(Head,req3))
 (Constraint FinishedBy(Head,req4))
 (Constraint During(Head,req5))
 (Constraint During(Head,req6))
)

##################################
# Eat (Food::OneBite) - contains #
##################################
(SimpleOperator
 (Head Human::Eat())
 (RequiredState req1 Food::OneBite())
 (Constraint Contains(Head,req1))
)

###############################
# Drink::Pour (PourS & PourE) #
###############################
(SimpleOperator
 (Head Drink::Pour())
 (RequiredState req1 Location::Kitchen())
 (RequiredState req2 Gesture::PourS())
 (RequiredState req3 Gesture::PourE())
 (RequiredState req4 Glass::On())
 (RequiredState req5 Bottle::On())
 (Constraint During(Head,req1))
 (Constraint StartedBy(Head,req2))
 (Constraint FinishedBy(Head,req3))
 (Constraint During(Head,req4))
 (Constraint During(Head,req5))
)

################################
# Eat (Drink::Pour) - contains #
################################
(SimpleOperator
 (Head Human::Eat())
 (RequiredState req1 Drink::Pour())
 (Constraint Contains(Head,req1))
)

###########################################
# Drink::OneGulp (PickUp & PutDown glass) #
###########################################
(SimpleOperator
 (Head Drink::OneGulp())
 (RequiredState req1 Location::Kitchen())
 (RequiredState req2 Gesture::PickUp())
 (RequiredState req3 Gesture::PutDown())
 (RequiredState req4 Glass::On())
 (Constraint During(Head,req1))
 (Constraint StartedBy(Head,req2))
 (Constraint FinishedBy(Head,req3))
 (Constraint During(Head,req4))
)

###################################
# Eat (Drink::OneGulp) - contains #
###################################
(SimpleOperator
 (Head Human::Eat())
 (RequiredState req1 Drink::OneGulp())
 (Constraint Contains(Head,req1))
)

######################
# PrepareMeal (oven) #
######################
(SimpleOperator
 (Head Human::PrepareMeal())
 (RequiredState req1 Location::Kitchen())
 (RequiredState req2 Oven::Open())
 (Constraint During(Head,req1))
 (Constraint Contains(Head,req2))
)

########################
# PrepareMeal (fridge) #
########################
(SimpleOperator
 (Head Human::PrepareMeal())
 (RequiredState req1 Location::Kitchen())
 (RequiredState req2 Fridge::Open())
 (Constraint During(Head,req1))
 (Constraint Contains(Head,req2))
)


#(SimpleOperator
# (Head RobotGoals::ReachPersonWhenPrepareMeal())
# (RequiredState req1 Human::PrepareMeal())
# (RequiredState req3 Robot::ReachPerson())
# (Constraint During(req3,Head))
# (Constraint Starts(req1,Head))
#)


##########################
# PrepareMeal (cupboard) #
##########################
(SimpleOperator
 (Head Human::PrepareMeal())
 (RequiredState req1 Location::Kitchen())
 (RequiredState req2 Cupboard::Open())
 (Constraint During(Head,req1))
 (Constraint Contains(Head,req2))
)

###############################
# PrepareMeal (microwaveOven) #
###############################
(SimpleOperator
 (Head Human::PrepareMeal())
 (RequiredState req1 Location::Kitchen())
 (RequiredState req2 MicrowaveOven::Open())
 (Constraint During(Head,req1))
 (Constraint Contains(Head,req2))
)

################
# Climb stairs #
################
(SimpleOperator
 (Head Human::ClimbStairs())
 (RequiredState req1 Posture::Standing())
 (RequiredState req2 Gesture::Walk())
 (RequiredState req3 Location::Staircase())
 (RequiredState req4 Location::EntranceHall())
 (Constraint During(Head,req1))
 (Constraint During(Head,req2))
 (Constraint Equals(Head,req3))
 (Constraint MetByOrAfter(Head,req4))
)

##################
# Descend stairs #
##################
(SimpleOperator
 (Head Human::DescendStairs())
 (RequiredState req1 Posture::Standing())
 (RequiredState req2 Gesture::Walk())
 (RequiredState req3 Location::Staircase())
 (RequiredState req4 Location::LaundryRoom())
 (Constraint During(Head,req1))
 (Constraint During(Head,req2))
 (Constraint Equals(Head,req3))
 (Constraint MetByOrAfter(Head,req4))
)

########
# Walk #
########
(SimpleOperator
 (Head Human::Walk())
 (RequiredState req1 Posture::Standing())
 (RequiredState req2 Gesture::Walk())
 (Constraint During(Head,req1))
 (Constraint Equals(Head,req2))
)
